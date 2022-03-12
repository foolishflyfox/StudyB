# 第六章 线程池与 Future

## 线程池的实现原理

调用方不断地向线程池中提交任务；线程池中有一组线程，不断地从队列中取任务，这是一个典型的生成者-消费者模型。

要实现这样一个线程池，有几个问题需要考虑：
1. 队列设置多长？如果是无界的，调用方不断地往队列中放任务，可能导致内存耗尽。如果是有界的，当队列满了之后，调用方如何处理？
2. 线程池中的线程个数是固定的还是动态变化的？
3. 每次提交新任务，是放入队列？还是开新线程？
4. 当没有任务的时候，线程时是睡眠一小段时间？还是进入阻塞？如果进入阻塞，要如何唤醒？

针对问题4，有3种做法：
1. 不使用阻塞队列，只使用一般的线程安全的队列，也无阻塞-唤醒机制。当队列为空时，线程池中的线程只能睡眠一会，然后醒来看队列中有没有新任务来，如此不断轮询。
2. 不使用阻塞队列，但在队列外部、线程池内部实现了阻塞-唤醒机制。
3. 使用阻塞队列。

很显然，做法3是最完善的，即避免了线程池内部自己实现阻塞-唤醒机制的麻烦，也避免了做法1的睡眠-轮询带来的资源消耗和延迟。正因为如此，接下来要讲的 ThreadPoolExecutor / ScheduledThreadPoolExecutor 都是基于阻塞队列来实现的，而不是一般的队列，至此，各式各样的阻塞队列就该排上用场了。

在正式了解线程池的实现原理之前，先对线程池的类继承体系进行一个宏观介绍：

- (Interface) Executor
    - (Interface) ExecutorService
        - (Interface) ScheduledExecutorService
        - (Class) AbstractExecutorService
            - (Class) ThreadPoolExecutor

ScheduledThreadPoolExecutor extends ThreadPoolExecutor implements ScheduledExecutorService

这里有两个核心的类：ThreadPoolExecutor 和 ScheduledThreadPoolExecutor，后者不仅可以执行某个任务，还可以周期性地执行任务。

向线程池中提交的每个任务，都必须实现 Runnable 接口，通过最上面的 Executor 接口中的 execute (Runnable Command)向线程池提交任务。

在 ExecutorService 中，定义了线程池的关闭接口 shutdown，还定义了可以有返回的任务，也就是 Callable，接下来的章节会详细介绍。

## 6.3 ThreadPoolExecutor

### 核心数据结构

基于线程池的实现原理，下面看一下 ThreadPoolExecutor 的核心数据结构。

```java
public class ThreadPoolExecutor extends AbstractExecutorService {
    // 状态变量
    private final AtomicInteger ctl = new AtomicInteger(ctlOf(RUNNING, 0));
    // 存放任务的阻塞队列
    private final BlockingQueue<Runnable> workQueue;
    // 对线程池内部各种变量进行互斥访问控制
    private final ReentrantLock mainLock = new ReentrantLock();
    // 线程集合
    private final HashSet<Worker> workers = new HashSet<Worker>();
}
```
每一个线程是一个 Worker 对象。Worker 是 ThreadPoolExecutor 的内部类，核心数据结构如下：
```java
private final class Worker
        extends AbstractQueuedSynchronizer
        implements Runnable{
    // Worker 封装的线程
    final Thread thread;
    // Worker 接收到的第一个任务
    Runnable firstTask;
    // Worker 执行完毕的任务个数
    volatile long completedTasks;
```
由定义会发现，Worker 继承与 AQS，也就是说 Worker 本身就是一把锁。这把锁有什么用处呢？在接下来分析线程池的关闭、线程执行任务的过程中会了解到。

### 核心配置参数解释

针对本章最开始提出的线程池实现的几个问题，ThreadPoolExecutor 在其构造函数中提供了几个核心配置参数，来配置不同策略的线程池。了解了每个参数的含义，也就明白了线程池的各种不同的策略。

```java
    public ThreadPoolExecutor(int corePoolSize,
                              int maximumPoolSize,
                              long keepAliveTime,
                              TimeUnit unit,
                              BlockingQueue<Runnable> workQueue,
                              ThreadFactory threadFactory,
                              RejectedExecutionHandler handler) {
        if (corePoolSize < 0 ||
            maximumPoolSize <= 0 ||
            maximumPoolSize < corePoolSize ||
            keepAliveTime < 0)
            throw new IllegalArgumentException();
        if (workQueue == null || threadFactory == null || handler == null)
            throw new NullPointerException();
        this.acc = System.getSecurityManager() == null ?
                null :
                AccessController.getContext();
        this.corePoolSize = corePoolSize;
        this.maximumPoolSize = maximumPoolSize;
        this.workQueue = workQueue;
        this.keepAliveTime = unit.toNanos(keepAliveTime);
        this.threadFactory = threadFactory;
        this.handler = handler;
    }
```
上面的各个参数，解释如下：
- corePoolSize: 在线程池中始终维护的线程个数；
- maxPoolSize：在 corePoolSize 已满，队列也满的情况下，扩充线程至此值；
- keepAliveTime / unit：在 maxPoolSize 中的空闲线程，销毁锁需要的时间，总线程数收缩会 corePoolSize；
- blockingQueue：线程池所用的队列类型；
- threadFactory：线程创建工厂，也可以自定义，有一个默认的；
- handler： corePoolSize 已满，队列已满，maxPoolSize 已满，最后的拒绝策略。

下面来看这 6 个配置参数在任务的提交过程中是怎么运作的。在每次往线程池中提交任务的时候，有如下的处理流程：
step1：判断当前线程数是否大于或等于 corePoolSize。如果小于，则新建线程执行，如果大于，则进入 step2；
step2：判断队列是否已满。如未满，则放入；如已满，则进入 step3；
step3：判断当前线程数是否小于 maxPoolSize，如果小于，则新建线程执行，否则进入 step4；
step4：根据拒绝策略，拒绝任务。

总结一下：首先判断 corePoolSize，其次判断 blockingQueue 是否已满，接着判断 maxPoolSize，最后使用拒绝策略。

很显然，基于这种流程，如果队列是无界的，将永远没有机会走到 step3，即 maxPoolSize 没有使用，也约定不会走到 step4。

### 线程池的优雅关闭

线程池的关闭，较之线程的关闭更加复杂。当关闭一个线程池的时候，有的线程还正在执行某个任务，有的调用者正在向线程池提交任务，并且队列中可能还有未执行的任务。因此，关闭过程不可能是瞬时的，而是需要一个平滑地过渡，这就涉及到线程池的完整生命周期管理。

#### 线程池的生命周期

在 JDK 7 中，把线程数量(workerCount)和线程池状态(runState)这两个变量打包存储在一个字段中，即 ctl 变量。最高的3位存储线程池状态，其余 29 位存储线程个数。而在 JDK 6 中，这两个变量时分开存储的。
```java
    private final AtomicInteger ctl = new AtomicInteger(ctlOf(RUNNING, 0));
    private static final int COUNT_BITS = Integer.SIZE - 3;
    private static final int CAPACITY   = (1 << COUNT_BITS) - 1;

    // runState is stored in the high-order bits
    private static final int RUNNING    = -1 << COUNT_BITS;
    private static final int SHUTDOWN   =  0 << COUNT_BITS;
    private static final int STOP       =  1 << COUNT_BITS;
    private static final int TIDYING    =  2 << COUNT_BITS;
    private static final int TERMINATED =  3 << COUNT_BITS;

    // Packing and unpacking ctl
    private static int runStateOf(int c)     { return c & ~CAPACITY; }
    private static int workerCountOf(int c)  { return c & CAPACITY; }
    private static int ctlOf(int rs, int wc) { return rs | wc; }
```
线程池状态有5种：RUNNING、SHUTDOWN、STOP、TIDYING 和 TERMINATED。

Running -> Shutdown: shutdown()
Running -> Stop: shutdownNow()
Shutdown -> Stop: shutdownNow()
Shutdown -> Tidying: 队列和线程池为空
Stop -> Tidying: 队列和线程池为空
Tidying -> Terminated: terminated()

线程池有两个关闭函数，shutdown() 和 shutdownNow()，这两个函数会让线程池切换到不同的状态。在队列为空，线程池也为空之后，进入 Tidying 状态；最终执行一个钩子函数 terminated()，进入 Termiated 状态，线程池才寿终正寝。

这里的状迁移有一个非常关键的特征，从小到大迁移，-1、0、1、2、3，只会从小的状态值往大的状态值迁移，不会逆向迁移。例如，当线程池的状态在 Tidying=2 时，接下来只能迁移到 Terminated=3，不可能迁移会 Stop=1或其他状态。

除了 terminated() 之外，线程池还提供了其他几个钩子函数，这些函数的实现都是空的，如果想要实现自己的线程池，可以重写这几个函数。
```java
protected void beforeExecute(Thread t, Runnable r) { }
protected void afterExecute(Runnable r, Throwable t) { }
```

#### 正确关闭线程池的步骤

通过上面的分析，我们知道了线程池的关闭需要一个过程，在调用 shutDown() 或者 shutdownNow() 之后，线程池并不会立即关闭，接下来需要调用 awaitTermination 来等待线程池关闭。关闭线程池的正确步骤如下：
```java
executor.shutdown();
// executor.shutdownNow(); // 或者
// 调完上面的操作之后，再循环调用 awaitTermination，等待线程池真正终止
try {
    boolean loop = true;
    do {
        // 等待所有任务完成
        loop = !executor.awaitTermination(2, TimeUnit.SECOND);
        // 阻塞，直到线程池里所有任务结束
    } while (loop);
} catch (InterruptedException e) {
    ...
}
```
awaitTermination(...) 函数的内部实现很简单，如下所示，不断循环判断线程池是否到达了最终状态 TERMINATED，如果是，返回 true，否则通过 termination 条件变量阻塞一段时间，苏醒后继续判断。
```java
public boolean awaitTermination(long timeout, TimeUnit unit) throw InterruptedExceptioin {
    long nanos = unit.toNanos(timeout);
    final ReentrantLock mainLock = this.mainLock;
    mainLock.lock();
    try {
        for (;;) {
            if (runStateAtLeast(ctl.get(), TERMINATED)) {
                return true;
            }
            if (nanos <= 0) {
                return false;
            }
            nanos = termination.awaitNanos(nanos);
        }
    } finally {
        mainLock.unlock();
    }
}
```
#### shutdown() 与 shutdownNow() 的区别

shutdown 不会清空任务队列，会等所有任务执行完成，shutdownNow 会清空任务队列。shutdown 只会中断空闲的线程，后者会中断所有线程。
```java
public void shutdown() {
    final ReentrantLock mainLock = this.mainLock;
    mainLock.lock();
    try {
        checkShutdownAccess();
        advanceRunState(SHUTDOWN);
        interruptIdleWorks();
        onShutdown();  // ScheduledThreadPoolExecutor 的钩子方法
    } finally {
        mainLock.unlock();
    }
    tryTerminate();
}
public List<Runnable> shutdownNow() {
    List<Runnable> tasks;
    final ReentrantLock mainLock = this.mainLock;
    mainLock.lock();
    try {
        checkShutdownAccess();
        advanceRunState(STOP);
        interruptWorkers();
        tasks = drainQueue();
    } finally {
        mainLock.unlock();
    }
    tryTerminate();
    return tasks;
}
```
下面看一下在上面的代码里中断空闲线程和中断所有线程的区别：
```java
private void interruptIdleWorkers (boolean onlyOne) {
    final ReentrantLock mainLock = this.mainLock;
    mainLock.lock();
    try {
        for (Worker w : workers) {
            if (!t.isInterrupted() && w.tryLock()) {
                try {
                    t.interrupt();
                } catch (SecurityException ignore) {
                } finally {
                    w.unlock();
                }
            }
            if (onlyOne) {
                break;
            }
        }
    } finally {
        mainLock.unlock();
    }
}
private void interruptWorkers() {
    final ReentrantLock mainLock = this.mainLock;
    mainLock.lock();
    try {
        for (Worker w : workers) {
            w.interruptIfStarted();
        }
    } finally {
        main.unlock();
    }
}
```
关键区别点在于 tryLock()，一个线程在执行一个任务之前，会先加锁，这意味着通过是否持有锁，可以判断出线程是否处于空闲状态。tryLock() 如果成功，说明线程处于空闲状态，向其发送中断信号；否则不发送。

在上面的代码中，shutdown() 和 shutdownNow() 都调用了 tryTerminate() 函数，如下所示：
```java
final void tryTerminate() {
    for (;;) {
        int c = ctl.get();
        if (isRunning(c) || runStateAtLeast(c, TIDYING) || (runStateOf(c) == SHUTDOWN && !workQueue.isEmpty())) {
            return;
        }
        if (workerCountOf(c) != 0) {
            interruptIdleWrokers(ONLY_ONE);
            return ;
        }
        // 当 workQueue 为空，workCount 为 0 时才会走到这里
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            if (ctl.compareAndSet(c, ctlOf(TIDYING, 0))) {  // 把状态切换到 TIDYING
                try {
                    terminated();  // 调用钩子函数
                } finally {
                    ctl.set(ctlOf(TERMINATED, 0));  // 状态由 TIDYING 改为 TERMINATED
                    termination.signalAll();  // 通知 awaitTermination(...)
                }
                return;
            }
        } finally {
            mainLock.unlock();
        }
    }
}
```
tryTerminate() 不会强行终止线程池，只是做了一下检测：当 workerCount 为0，workerQueue 为空时，先把状态切换到 TIDYING，然后调用钩子函数 terminated()。当钩子函数执行完成时，把状态从 TIDYING 变为 TERMINATED，接着调用 termination.signalAll()，通知前面阻塞在 awaitTermination 的所有调用者线程。

所以，TIDYING 和 TERMINATED 的区别是在二者之间执行了一个钩子函数 terminated()，目前是一个空实现。

### 任务的提交过程分析

提交任务的函数如下：
```java
public void execute(Runnable command) {
    if (command == null) {
        throw new NullPointException();
    }
    int c = ctl.get();
    // 如果当前的线程数小于 corePoolSize，则开新线程
    if (workerCountOf(c) < corePoolSize) {
        if (addWorker(command, true))
            return ;
        c = ctl.get();
    }
    // 如果当前的线程数大于或等于 corePollSize，则调用 workQueue.offer 放入队列
    if (isRunning(c) && workQueue.offer(command)) {
        int recheck = ctl.get();
        if (!iRunning(recheck) && remove(command)) {
            reject(command);
        } else if (workerCountOf(recheck) == 0) {  // 放入队列失败，开新线程
            addWorker(firstTask, false);
        } else i (!addWorker(command, flase)) {  // 线程数大于 maxPoolSize，调用拒绝策略
            reject(command);
        }
    }
}

// 此函数用于开一个新线程，如果第二个参数 core 为 true，则用 corePoolSize 作为上界；如果为 false
// 则用 maxPoolSize 作为上界
private boolean addWorker(Runnable firstTask, boolean core) {
    retry:
    for (;;) {
        int c = ctl.get();
        int rs = runStateOf(c);

        // 只要状态大于或等于 SHUTDOWN，说明线程池进入了关闭过程
        if (rs >= SHUTDOWN && !(rs == SHUTDOWN && firstTask == null && !workQueue.isEmpty())) {
            return false;
        }

        for (;;) {
            int wc = workerCountOf(c);
            if (wc >= CAPACITY || wc >= (core ? corePoolSize : maximumPoolSize))
                retur false; // 线程数超过上界 corePoolSize 或 maxPoolSize，不会开新线程，直接返回 false
            if (compareAndIncrementWrokerCount(c))  // workCount 成功加1，跳出整个 for 循环
                break retry;
            c = ctl.get();
            if (runStateOf(c) != rs)
                continue retry;
        }
    }
    // workCount 成功加1，开始添加线程操作
    boolean workerStarted = false;
    boolean workerAdded = false;
    Worker w = null;
    try {
        w = new Worker(firstTask);  // 创建一个线程
        final Thread t = w.thread;
        if (t != null) {
            final ReentrantLock mainLock = this.mainLock;
            mainLock.lock();
            try {
                int rs = runStateOf(ctl.get());
                if (rs < SHUTDOWN || (rs == SHUTDOWN && firstTask == null)) {
                    if (t.isAlive())
                        throw new IllegalThreadStateException();
                    workers.add(w);  // 把线程加入线程集合
                    int s = worker.size();
                    if (s > largestPoolSize)
                        largestPoolSize = s;
                    workerAdded = true;
                }
            } finally {
                mainLock.unlock();
            }
            if (workerAdded) {
                t.start();  // 若加入成功，则启动该线程
                workerStarted = true;
            }
        }
    } finally {
        if (! workerStarted)  // 加入失败
            addWorkerFailed(w); // 把 workCount 减1
    }
    return workerStarted;
}
```

### 任务的执行过程分析

在上面的任务提交过程中，可能会开启一个新的 Worker，并把任务本身作为 firstTask 赋给该 Worker。但对于一个 Worker 来说，不是只执行一个任务，而是源源不断地从队列中取任务执行，这是一个不断循环的过程。

下面来看 Worker 的 run 方法的实现过程。
```java
private final class Worker extends AbstractQueueSynchronizer implement Runnable{
    Worker (Runnable firstTask) {
        setState(-1); // 初始状态是 -1
        this.firstTask = firstTask;
        this.thread = getThreadFactory().newThread(this);
    }
    public void run() {
        runWorker(this);
    }
    // 核心函数，ThreadPoolExecutor 的 runWorker(Worker w)
    final void runWorker(Worker w) {
        
    }
}
```

