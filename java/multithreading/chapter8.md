# 线程管理

## 线程组

线程组(ThreadGroup)类可以用来表示一组相似(相关)的线程。线程与线程组之间的关系类似文件与文件夹之间的关系。一个线程组可以包含多个线程以及其他线程组。一个线程组包含其他线程组的时候，该线程组称为这些线程组的父线程组。如果创建线程的时候我们没有指定线程组，那么这个线程就属于其父线程（即当前线程）所属的线程组。

由于 Java 虚拟机在创建 main 线程时会为其指定一个线程组，因此 Java 平台中的任何一个线程都有一个线程组与之关联。该线程组可以通过 Thread.getThreadGroup() 调用来获取。

多数情况下，我们可以忽略线程组这一概念以及线程组的存在。

## 可靠性：线程的未捕获异常与监控

如果线程的 run 方法抛出未被捕获的异常(Uncaught Exception),那么随着 run 方法的退出，相应的线程也提前终止。对于线程的这种异常终止，我们如何得知并做出可能的补救动作，例如重新创建并启动一个替代线程呢？

JDK 1.5 为了解决这个问题，引入了 UncaughtExceptionHandler 接口。该接口是在 Thread 内部定义的，它只定义了一个方法，`void uncaughtException(Thread t, Throwable e)`。

uncaughtException 方法中的两个参数包括异常终止的线程本身(对应第一个参数)以及导致线程提前终止的异常(对应第2个参数)。那么，在 uncaughtException 方法中，我们就可以做一些有意义的事情，比如将线程异常终止的相关信息记录在日志文件中，甚至为异常终止的线程启动一个替代线程。设 thread 为任意一个线程，eh 为任意一个 UncaughtExceptionHandler 实例，那么我们可以在启动 thread 前通过调用 thread.setUncaughtExceptionHandler(eh)来为thread关联一个UncaughtExceptionHandler。当 thread 抛出未被捕获的异常后，thread.run() 返回，接着 thread 会在其终止前调用 eh.uncaughtException 方法。

下面是 UncaughtExceptionHandler 实现的线程监控代码：
```java
public class ThreadMonitorDemo {
    volatile boolean inited = false;
    static int threadIndex = 0;
    final BlockingQueue<String> channel = new ArrayBlockingQueue<String>(100);

    public static void main(String[] args) throws InterruptedException{
        ThreadMonitorDemo demo = new ThreadMonitorDemo();
        demo.init();
        for (int i=0; i<100; ++i){
            demo.service("test-"+i);
        }
        Thread.sleep(2000);
        System.exit(0);
    }
    public void service(String message) throws InterruptedException {
        channel.put(message);
    }
    public synchronized void init() {
        if (inited) {
            return;
        }
        System.out.println("init...");
        WorkerThread t = new WorkerThread();
        t.setName("Worker0-"+threadIndex++);
        // 为线程t关联一个UncaughtExceptionHandler
        t.setUncaughtExceptionHandler(new ThreadMonitor());
        t.start();
        inited = true;
    }
    private class ThreadMonitor implements Thread.UncaughtExceptionHandler {
        @Override
        public void uncaughtException(Thread t, Throwable e) {
            System.out.printf("Current thread is `t`: %s, it is stall alive: %s\n",
                    Thread.currentThread()==t, t.isAlive());
            String threadInfo = t.getName();
            // 创建并启动替代线程
            inited = false;
            init();
        }
    }
    private class WorkerThread extends Thread {
        @Override
        public void run() {
            System.out.println("Do something important...");
            String msg;
            try {
                for (;;) {
                    msg = channel.take();
                    process(msg);
                }
            } catch (InterruptedException e) {
            }
        }
        private void process(String message) {
            System.out.println(message);
            if ((int) (Math.random() * 100) < 2) {
                throw new RuntimeException("test");
            }
            Tools.randomPause(100);
        }
    }
}
```
在这个例子中，系统的某个重要服务(ThreadMonitorDemo)内部维护了一个工作者线程(WorkThread)用于实现该服务的核心功能。因此，一旦该工作者线程由于某些未捕获的异常而提前终止，我们需要第一时间得到通知，并为该线程创建并能启动一个替代线程来接替其完成任务，以保障服务的可靠性。

在执行 UncaughtExceptionHandler.uncaughtException 方法时，线程 t 还是存活的，该方法返回后，t 就终止了。

## 有组织有纪律：线程工厂

ThreadFactory 接口是工厂方法模式的一个实例，它定义了如下工厂方法：`public Thread newThread(Runnable r)`。newThread 方法可以用来创建线程，该方法的参数 r 表示所创建的线程需要执行的任务。

如果把线程对象看作某种产品，那么通过 new 方式创建线程好比手工制作，而使用 ThreadFactory 接口创建线程则好比是工厂采用标准化的流水线进行生产。我们可以在 ThreadFactory.newThread 方法中封装线程创建的逻辑，这使得我们能够以统一的方式为线程创建、配置做一些非常有用的动作。

如下列代码所示，ThreadFactory 实现类 XThreadFactory 的 newThread 方法为其创建的每一个线程做了这样一系列的处理逻辑：为线程关联 UncaughtExceptionHandler，为线程设置一个含义更加具体的有助于问题定位的名称，确保线程是一个用户线程，确保线程的优先级别为正常级别，以及在线程创建的时候打印相关的日志信息。并且，这些线程的 toString() 返回值更加有利于问题的定位——在对真实的(商用)多线程系统中的问题进行定位的过程中，将一个线程与另一个线程区别开来非常有助于问题的定位，线程 ID 以及线程对象的身份标识(Hash Code)是将一个线程与另一个线程区分开来的重要依据，而 Thread.toString() 的返回值并没有体现这一点。可见，XThreadFactory 不仅为我们提供了一个新的线程，它还为我们这个线程做了一些有利于简化客户端代码以及有利于代码调试和问题定位的动作。

```java
public class XThreadFactory implements ThreadFactory {
    private final Thread.UncaughtExceptionHandler ueh;
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    // 所创建的线程的线程名前缀
    private final String namePrefix;

    public XThreadFactory(Thread.UncaughtExceptionHandler ueh, String name) {
        this.ueh = ueh;
        this.namePrefix = name;
    }
    public XThreadFactory() {
        this(new LoggingUncaughtExceptionHandler(), "thread");
    }
    protected Thread doMakeThread(final Runnable r) {
        return new Thread(r) {
            @Override
            public String toString() {
                // 返回对问题定位更加有益的信息
                ThreadGroup group = getThreadGroup();
                String groupName = null == group ? "" : group.getName();
                String threadInfo = getClass().getSimpleName() + "[" + getName() + ","
                        + getId() + "," + groupName + "] @" + hashCode();
                return threadInfo;
            }
        };
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = doMakeThread(r);
        t.setUncaughtExceptionHandler(ueh);
        t.setName(namePrefix + "-" + threadNumber.getAndIncrement());
        if (t.isDaemon()) {
            t.setDaemon(false);
        }
        if (t.getPriority() != Thread.NORM_PRIORITY) {
            t.setPriority(Thread.NORM_PRIORITY);
        }
        return t;
    }

    static class LoggingUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

        @Override
        public void uncaughtException(Thread t, Throwable e) {
            System.out.println(t + " terminated: " + e);
        }
    }
}
```

## 线程的暂挂与恢复

Thread.suspend()、Thread.resume() 两个方法都是已废弃的方法。其作用分别是暂挂和恢复线程。

暂挂与暂停的含义基本相同，它更多地是指用户感知得到的线程暂停；恢复和唤醒含义基本系统。我们可以采用与停止线程相似的思想来实现线程的暂挂与恢复：设置一个线程暂挂标志，那么线程就执行 Object.wait()/Condition.await() 暂停，直到其他线程重新设置暂挂标志并将其唤醒。根据该思路，我们可以设计一个用于控制线程的暂挂与恢复的工具类 PauseControl，如下所示：
```java
public class PauseControl extends ReentrantLock {
    private static final long serialVersionUID = 2174308048547217616L;
    // 线程暂挂标志
    private volatile boolean suspend = false;
    private final Condition condSuspended = newCondition();

    /**
     * 暂停线程
     */
    public void requestPause() {
        suspend = true;
    }
    /**
     * 恢复线程
     */
    public void proceed() {
        lock();
        try {
            suspend = false;
            condSuspended.signalAll();
        } finally {
            unlock();
        }
    }
    /**
     * 当前线程仅在线程暂挂标记不为 true 时才执行指定的目标动作
     */
    public void pauseIfNecessary(Runnable targetAction) throws InterruptedException {
        lock();
        try {
            while (suspend == false) {
                condSuspended.wait();
            }
            targetAction.run();
        } finally {
            unlock();
        }
    }
}
```

## 线程的高效利用

线程是一种昂贵的资源，其主要开销包括：
- 线程的创建与启动的开销。与普通对象相比，Java 线程还占用了额外的存储空间——栈空间。并且，线程的启动会产生相应的线程调度开销。
- 线程的销毁。
- 线程的调度开销。

一种有效使用线程的方式是线程池。

常见的对象池(比如数据库连接池)的实现方式是对象池内部维护一定数量的对象，客户端代码需要一个对象的时候就向对象池申请一个对象，用完之后再返还给对象池。

线程池本身也是一个对象，不过它的实现与普通对象池不同。线程池内部可以预先创建一定数量的工作者线程，客户端代码并不需要向线程池借用线程，而是将其需要执行的任务作为一个对象提交给线程池，线程池可能将这些任务缓存在队列(工作队列)之中，而线程池内部的各个工作者线程则不断地从队列中取出任务并执行。因此，线程池可以看成是基于生产者-消费者模式的一种服务，该服务内部维护的工作者线程相当于消费者线程，线程池的客户端线程相当于生产者线程，客户端代码提交给线程池的任务相当于“产品”，线程池内部用于缓存任务的队列相当于传输通道。

java.util.concurrent.ThreadPoolExecutor 类就是一个线程池，客户端代码可以调用 `ThreadPoolExecutor.submit` 方法向其提交任务，ThreadPoolExecutor.submit 方法声明如下:`public Future<?> submit(Runnable task)`。其中 task 参数是一个 Runnable 实例，它代表客户端需要线程池代为执行的任务。为了方便讨论，我们先忽略该方法的返回值。

线程池内部维护的工作者线程的数量被称为该线程池的线程池大小，ThreadPoolExecutor 的线程池大小有 3 种形态：当前线程池大小(Current Pool Size)表示线程池中实际工作者线程的数量；最大线程池大小(Maximum Pool Size)表示线程池中允许存在的工作者线程的数量上限；核心线程池大小(Core Pool Size)表示一个不大于最大线程池大小的工作者线程数量上限。

它们组件的数量关系为：当前线程池大小 ≤ 核心线程池大小 ≤ 最大线程池大小；或 核心线程池大小 ≤ 当前线程池大小 ≤ 最大线程池大小；

这里，除了当前线程池大小是对线程池中现有的工作者线程进行技术的结果，其他有关线程池大小的概念实际上都是开发人员或者系统配置参数指定的一个阈值(Threshold)。这些阈值的具体含义下文会介绍。

ThreadPoolExecutor 的构造器中包含参数最多的一个构造器声明为：
```java
public ThreadPoolExecutor(int corePoolSize,
                int maximumPoolSize,
                long keepAliveTime,
                TimeUnit unit,
                BlockingQueue<Runnable> workQueue,
                ThreadFactory threadFactory,
                RejectedExecutionHandler handler)
```
其中，workQueue 是被称为工作队列的阻塞队列，它相当于生产者——消费组模式中的传输通道，corePoolSize 用于指定线程池核心大小，maximumPoolSize 用于指定最大线程池大小。keepAliveTime 和 unit 合在一起用于指定线程池中空闲(Idle)线程的最大存活时间。threadFactory 指定用于创建工作者线程的线程工厂，handler 参数下面会介绍。

在初始状态下，客户端没提交一个任务，线程池就创建一个工作者线程来处理该任务。随着客户端不断地提交任务，当前线程池大小也相应增加。在当前线程池大小达到核心线程池大小的时候，新来的任务会被存入工作队列中。这些缓存的任务由线程池中的所有工作者线程负责取出并执行。

线程池将任务存入工作队列的时候，调用的是 BlockingQueue 的非阻塞方法 offer(E e)，因此工作队列满并不会使提交任务的客户端线程暂停。当工作队列满的时候，线程池会继续创建新的工作者线程，直到当前线程大小达到最大线程池大小。

例如下面的一个例子：
```java
public class T8 {
    public static void main(String[] args) {
        AtomicInteger i = new AtomicInteger(0);
        ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 3, 1000, TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(2),
                r -> new Thread(r, "thread-"+i.get()),
                (r, e) -> System.out.println(System.currentTimeMillis() + ": rejected Execution"));
        for (; i.get() < 8; i.incrementAndGet()) {
            System.out.printf("%s: submit task\n", System.currentTimeMillis());
            executor.submit(new MyTask("task-" + i.get()));
            Tools.sleep(3000);
        }
    }
    static class MyTask implements Runnable {
        public String taskName;
        public MyTask(String taskName) {
            this.taskName = taskName;
        }
        @Override
        public void run() {
            System.out.printf("%s: %s[%s] start\n", System.currentTimeMillis(),
                    Thread.currentThread().getName(), taskName);
            Tools.sleep(17000);
            System.out.printf("%s: %s[%s] end\n", System.currentTimeMillis(),
                    Thread.currentThread().getName(), taskName);
        }
    }
}
```
该例子的输出为：
```
1637111004459: submit task
1637111004478: thread-0[task-0] start
1637111007483: submit task
1637111010487: submit task
1637111013489: submit task
1637111013490: thread-3[task-3] start
1637111016490: submit task
1637111016491: thread-4[task-4] start
1637111019496: submit task
1637111019496: rejected Execution
1637111021482: thread-0[task-0] end
1637111021483: thread-0[task-1] start
1637111022499: submit task
1637111025503: submit task
1637111025503: rejected Execution
1637111030496: thread-3[task-3] end
1637111030496: thread-3[task-2] start
1637111033495: thread-4[task-4] end
1637111033495: thread-4[task-6] start
1637111038484: thread-0[task-1] end
1637111047501: thread-3[task-2] end
1637111050497: thread-4[task-6] end
```
下面按时间戳进行分析：

- 1637110292993: 提交了任务 task-0
- 1637111004478: 线程池创建线程 thread-0，并开始执行 task-0
- 1637111007483: 提交了任务 task-1，因为工作线程数已经达到核心线程池大小，因此 task-1 被存入任务队列中
- 1637111010487: 提交了任务 task-2，存入队列
- 1637111013489: 提交任务 task-3
- 1637111013490: 因为队列大小为2，因此队列已满，task-3 任务执行，工作线程数变为2
- 1637111016490: 提交任务 task-4
- 1637111016491: 队列满，task-4任务执行，工作线程变为3
- 1637111019496: 提交任务 task-5
- 1637111019496: 队列满，工作线程数为3，达到最大线程数，任务被丢弃
- 1637111021482: task-0 任务执行结束
- 1637111021483: 从队列中取出 task-1 开始执行，队列中任务数变为1
- 1637111022499: 提交任务 task-6，存入队列，队列变满
- 1637111025503: 提交任务 task-7
- 1637111025503: 队列满，任务被拒绝
- 1637111030496: task-3 执行结束
- 1637111030496: task-2 任务从队列中取出执行
- 1637111033495: task-4 执行结束
- 1637111033495: task-6 任务从队列中取出执行
- 1637111038484: task-1 执行结束
- 1637111047501: task-2 执行结束
- 1637111050497: task-6 执行结束

结论：
1. 当工作者线程 < 核心线程池大小时，来一个任务创建一个线程并执行；
2. 当工作者线程 == 核心线程池大小后，若任务队列没满，来一个任务，存入任务队列；
3. 当核心线程池 ≤ 工作者线程 < 最大线程池，且队列满时，来一个任务，创建一个线程，并执行该任务(不从队列中取任务)；
4. 当 工作者线程 == 最大线程池，且队列满时，新来的任务被丢弃；
5. 当有空闲线程时，从队列中取任务进行执行；

Java 标准库引入了一个 RejectedExecutionHandler 接口用于封装被拒绝的任务的处理策略，该接口仅定义了方法：`void rejectedExecution(Runnable r, ThreadPoolExecutor executor)`，其中 r 代表被拒绝的任务，executor 代表拒绝任务 r 的线程池实例。我们可以通过线程池的构造器参数 handler 或者线程池的 setRejectedExecutionHandler 方法来为线程池关联一个 RejectedExecutionHandler。当客户端提交的任务被拒绝时，线程池锁关联的 RejectedExecutionHandler 的 rejectedExecution 方法会被线程池调用。ThreadPoolExecutor 自身提供了几个现成的 RejectedExecutionHandler 接口实现类。其中 AbortPolicy 是默认的处理器。如果该处理器无法满足要求，优先考虑其他已提供的处理器，其次再考虑自行实现 RejectedExecutionHandler 接口。

- ThreadPoolExecutor.AbortPolicy: 直接抛出异常；
- ThreadPoolExecutor.DiscardPolicy: 丢弃当前被拒绝的任务(不抛出任何异常)；
- ThreadPoolExecutor.DiscardOldestPolicy: 将工作队列中最老的任务丢弃，然后重新尝试接纳被拒的任务；
- ThreadPoolExecutor.CallerRunsPolicy: 在客户端线程中执行被拒的任务；

ThreadPoolExecutor.prestartAllCoreThreads() 使得我们可以使线程池在未接收到任何任务的情况下预先创建并启动所有核心线程，这样可以减少任务被线程池处理时所需的等待时间(等待核心线程的创建与启动)。

ThreadPoolExecutor.shutdown() / shutdownNow() 方法可以用来关闭线程池。使用 shotdown() 关闭线程池的时候，已提交的任务会被继续，而新提交的任务会像线程池饱和那样被拒绝掉。ThreadPoolExecutor.shutdown() 返回的时候，线程池可能尚未关闭，即线程池中可能还有工作者线程正在执行。可以通过调用 ThreadPoolExecutor.awaitTermination(long timeout, TimeUnit unit) 来等待线程池关闭。使用 ThreadPoolExecutor.shutdownNow() 关闭线程池的时候，正在执行的任务会被停止，已经提交而等待执行的任务也不会被执行。
```java
public class T1 {
    public static void main(String[] args) {
        Runnable r = () -> {
            System.out.println(Thread.currentThread().getName() + " start");
            Tools.sleep(100000);
        };
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 2, 10,
                TimeUnit.SECONDS, new LinkedBlockingQueue<>(3));
        threadPoolExecutor.submit(r);
        threadPoolExecutor.submit(r);
        threadPoolExecutor.submit(r);
        Tools.sleep(1);
        List<Runnable> runnables = threadPoolExecutor.shutdownNow();
        System.out.println(runnables);

    }
}
```
执行结果为：
```
pool-1-thread-1 start
[java.util.concurrent.FutureTask@65ab7765, java.util.concurrent.FutureTask@1b28cdfa]
java.lang.InterruptedException: sleep interrupted
	at java.lang.Thread.sleep(Native Method)
	at com.bfh.Tools.sleep(Tools.java:9)
	at com.bfh.T1.lambda$main$0(T1.java:15)
	at java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:511)
	at java.util.concurrent.FutureTask.run(FutureTask.java:266)
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149)
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
	at java.lang.Thread.run(Thread.java:748)
```
说明了两点：
- 已经在执行的线程不会在 shutdownNow() 函数的返回列表中。
- shutdownNow 内部是通过调用工作者线程的 interrupt 方法来停止正在执行的任务的，因此，某些无法响应中断的任务可能永远也不会停止。

### 任务的处理结果、异常处理与取消

ThreadPoolExecutor 的另一个 submit 方法可以提交任务，该 submit 方法的声明如下：`public <T> Future<T> submit(Callable<T> task)`。task 参数代表客户端需要提交的任务，其类型为 java.util.concurrent.Callable。Callable 接口定义的唯一方法声明为：`V call() throw Exception`。Callable 接口也是对任务的抽象：任务的处理逻辑可以在 Callable 接口实现类的 call 方法中实现。Callable 接口相当于增强型的 Runnable 接口：call 方法的返回值代表相应任务的处理结果，其类型 V 是通过 Callable 接口的类型参数指定的；call 方法代表的任务再其执行过程中可以抛出异常。而 Runnable 接口中的 run 方法既无返回值，也不能抛出异常。

Future 接口实例可被看作提交给线程池执行的任务的处理结果句柄(Handle),Future.get 方法可以用来获取 task 参数锁指定的任务的处理结果，该方法声明为 `V get() throws InterruptedException,ExecutionExecution`。Future.get() 被调用时，如果相应的任务还未执行完毕，那么 Future.get() 会使当前线程暂停，直到相应的任务执行结束(包括正常结束和抛出异常而终止)。因此，Future.get() 是个阻塞方法，该方法能够抛出 InterruptedExecution 说明它可以相应线程中断。另外，假设相应的任务在执行过程中抛出一个任意的异常 originException，那么 Future.get（） 方法本身就会抛出相应的 ExecutionException 异常，调用这个异常的 getCause 方法可以返回 originalException 。因此，客户端代码可以通过捕获 Future.get() 调用抛出的异常来相应任务执行过程中抛出的异常。

由于在任务未执行完毕的情况下调用 Future.get() 方法来获取该任务的处理结果会导致等待并由此而导致的上下文切换，由此客户端代码应该尽可能早地向线程池提交任务，并尽可能晚地调用 Future.get() 方法来获取任务的处理结果，而线程池则刚好利用这段时间内来执行已提交的任务(包括我们关心的任务)。
```java
public class FutureDemo {
    public static void main(String[] args) {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 2, 10, TimeUnit.SECONDS, new LinkedBlockingDeque<>(3));
        Future<Integer> submit = executor.submit(() -> {
            System.out.println(System.currentTimeMillis() + ": thread start");
            Tools.sleep(3000);
            System.out.println(System.currentTimeMillis() + ": thread end");
            return 10;
        });
        System.out.println(System.currentTimeMillis() + ": after submit");
        try {
            Integer result = submit.get();
            System.out.println(System.currentTimeMillis() + ": result = " + result);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            Throwable realReasonn = e.getCause();
            realReasonn.printStackTrace();
        }
        executor.shutdown();
    }
}
```
执行结果为：
```
1637195912233: after submit
1637195912233: thread start
1637195915236: thread end
1637195915237: result = 10
```
Future 接口还支持任务的取消。为此，Future 接口定义了如下方法：`boolean cancel(boolean mayInterruptIfRunning);`。该方法的返回值表示相应的任务取消是否成功。任务取消失败的原因包括待取消的任务已经执行完毕或正在执行、已经被取消以及其他无法取消的因素。参数 mayInterruptIfRunning 表示是否允许通过给相应任务的执行线程发送中断来取消任务。Future.isCancelled() 返回值代表相应的任务是否被成功取消。由于一个任务被成功取消之后，相应的 Future.get() 调用会抛出 CancellationException 异常(运行时异常)，因此如果任务有可能被取消，那么在获取任务的处理结果前，我们需要下判断任务是否已经被取消了。

get 还有另一个版本：`V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException;` ，该方法作用与 Future.get() 相同，不过它允许我们指定一个等待超时时间，如果在该时间内相应的任务未执行结束，那么该方法就会抛出 TimeoutException。由于该方法参数指定的超时时间仅仅是控制客户端等待相应任务的处理结果最多会等多长时间，而非相应任务本身的执行时间限制。因此，客户端线程通常还要在捕获 TimeoutException 后执行 Future.cancel(true)来取消相应任务的执行。

### 线程池监控

尽管线程池的大小，工作队列的容量、线程空闲时间限制这些线程池属性可以通过配置的方式进行制定(而不是硬编码在代码中)，但是所指定的值是否恰当则需要通过监控来判断。例如，如果我们选择有界队列作为工作队列，那么这个队列的容量以多少为宜？这需要在软件测试过程中对线程池进行监控来确定。另外，考虑到测试环境和软件实际运行环境总是存在差别，出于软件运维的考虑，我们也需要对线程池进行监控。ThreadPoolExecutor 类提供了对线程池进行监控的相关方法。

- getPoolSize(): 获取当前线程池大小；
- getQueue(): 返回工作队列实例；
- getLargestPoolSize(): 获取工作者线程数曾经达到的最大数；
- getActiveCount(): 获取线程池中当前执行任务的工作者线程数(近似值)；
- getTaskCount(): 获取线程池到目前为止所接收到的任务数(近似值);
- getCompletedTaskCount(): 获取线程池到目前为止已经处理完毕的任务书(近似值);

此外，ThreadPoolExecutor 提供了两个钩子方法(Hook Method): beforeExecute(Thread t, Runnable r) 和 afterExecute(Thread t, Runnable r)也能够实现监控。设 executor 为任意一个 ThreadPoolExecutor 实例，在任意一个 r 被线程池 executor 中的任意一个工作者线程 t 执行前，executor.beforeExecute(t, r) 都会被执行，t 执行完 r 后，不管 r 的执行是否成功，还是抛出了异常，executor.afterExecute(t, r) 始终都会被执行。因此，如果有必要的话，我们可以通过创建 ThreadPoolExecutor 的子类并在子类的 beforeExecute 和 afterExecute 方法实现监控逻辑，比如计算任务执行的平均耗时。

### 工作者线程的异常终止

如果任务是通过 ThreadPoolExecutor.submit 调用提交给线程池的，那么这些任务在其执行过程中即便是抛出了未捕获的异常也不会导致对其进行执行的工作者线程异常终止。

如果任务时通过 ThreadPoolExecutor.execute 方法提交给线程池的，那么这些任务在其执行过程中一旦抛出了未捕获的异常，则对其进行执行的工作者线程就会异常终止。尽管，ThreadPoolExecutor 能够侦测到这种情况并在工作者线程异常终止的时候创建并启动新的替代工作者线程，但是，由于线程的创建于启动都有其开销，因此这种情形下我们会尽量避免任务在执行过程中抛出未捕获的异常。

我们可以通过 ThreadPoolExecutor 的构造参数或者 ThreadPoolExecutor.setThreadFactory 方法为线程池关联一个线程工厂。在这个线程工厂里面，我们可以为其创建的工作者线程关联一个 UncaughtExceptionHandler，通过这个关联的 UncaughtExceptionHandler 我们可以侦测到任务执行过程中抛出的未捕获异常。不过，由于 ThreadPoolExecutor 内部实现的原因，只有通过 ThreadPoolExecutor.execute 调用提交给线程池的任务，其执行过程中抛出的未捕获异常才会导致 UncaughtExceptionHandler.uncaughtException 方法被调用。


