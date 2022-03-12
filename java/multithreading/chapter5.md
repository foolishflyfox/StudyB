# 线程间协作

面向对象的世界中，类不是孤立的，一个类往往需要借助其他类才能完成一个计算，同样，多线程世界中的线程并不是孤立的，一个线程往往需要其他线程的协作才能够完成其待执行的任务。本章将介绍线程间的常见协作形式以及 Java 语言对这些协作所提供的支持。

## 等待与通知

在单线程编程中，程序要执行的操作如果需要满足一定条件(保护条件)才能执行，那么我们可以将该操作放在一个 if 语句体中，这使得目标动作只有在保护条件得以满足的时候才能被执行。在多线程编程中处理这种情形我们有另一种选择 -- 保护条件未满足可能只是暂时的，稍后其他线程可能更新了保护条件涉及的共享变量而使其成立，因此我们可以将当前线程暂停，直到其所需的保护条件满足再将其唤起。伪代码如下：
```java
atomic {
    while (保护条件不成立) {
        暂停当前线程;
    }
    doAction();
}
```
一个线程因为执行目标动作所需的保护条件未被满足而暂停的过程称为等待(wait)。一个线程更新了系统的状态，使得其他线程所需的保护条件得以满足的时候，唤醒那些被暂停的线程的过程 就被称为通知(notify)。

### wait/notify 的作用和用法

在 Java 平台中，Object.wait() / Object.wait(long) 以及 Object.notify() / Object.notifyAll() 可用于实现等待和通知：Object.wait() 的作用是使其执行线程被暂停(其生命周期状态变为 WAITING)，该方法可用来实现等待；Object.notify() 的作用是唤醒一个被暂停的线程，调用该方法可实现通知。

执行 Object.wait() 的线程被称为等待线程；Object.notify()的执行线程被称为通知线程。由于 Object 类是 Java 中任何对象的父类，因此使用 Java 中的任何对象都能够实现等待和通知。

使用 Object.wait() 实现等待，其代码模板如下伪代码所示：
```java
// 在调用 wait 方法前获得相应对象的内部锁
synchronized (someObject) {
    while (保护条件不成立) {
        // 调用 Object.wait() 暂停当前线程
        someObject.wait();
    }
    // 代码执行到这说明保护条件已经满足了
    // 执行目标动作
    doAction();
}
```
其中，保护条件是一个包含共享变量的布尔表达式。当这些共享变量被其他线程(通知线程)更新后，使相应的保护条件得以成立时，这些线程会通知等待线程。

由于一个线程只有在持有一个对象的内部锁的情况下才能够调用该对象的 wait 方法，因此 Object.wait() 调用总是在响应对象所引导的临界区中。包含上述模板代码的方法称为受保护方法(Guarded Method)，受保护方法包括3个要素：包含条件，暂停当前线程和目标动作。

设 someObject 为 Java 中任意一个类的实例，因执行 someObject.wait() 而被暂停的线程就称为对象 someObject 上的等待线程。由于同一个对象的同一个方法(someObject.wait())可以被多个线程执行，因此一个对象可能存在多个等待线程。someObject 上的的等待线程可以被多个线程执行，因此一个对象可能存在多个等待线程。someObject 上的等待线程可以通过其他线程执行 someObject.nofity() 来唤醒。someObject.wait() 会以原子操作的方式使其执行线程(当前线程)暂停并使该线程释放其持有的 someObject 对应的内部锁。

当前线程被暂停的时候，其对 someObject.wait() 的都调用并未返回。其他线程在该线程所需的保护条件成立的时候，执行相应的 notify 方法，即 someObject.notify() 可以唤醒 someObject 上的一个(任意的)等待线程。被唤醒的等待线程在其作用处理器继续运行的时候，需要再次申请 someObject 对应的内部锁。被唤醒的线程在其再次持有 someObject 对应的内部锁的情况下继续执行 someObject.wait() 中剩余的指令，直到 wait 方法返回。

另外，等待线程在其被唤醒，继续运行到其再次持有相应对象的内部锁的这段时间内，由于其他线程可能抢先获得相应的内部锁并更新了相关共享变量而导致该线程所需的保护条件又再次不成立，因此 Object.wait() 调用返回后，我们需要再次判断此配合条件是否成立。所以，对保护条件的判断以及 Object.wait() 调用应该放在循环语句中，以确保目标动作只有在保护条件成立的情况下才能够执行。

使用 Object.notify() 实现通知对的伪代码如下：
```java
synchronizedd(someObject) {
    // 更新等待线程的保护条件涉及的共享变量
    updateSharedState();
    // 唤醒其他线程
    someObject.notify();
}
```
Object.notify() 的执行线程持有的相应对象的内部锁只有在 Object.notify() 调用所在的临界区执行结束后再回被释放，而 Object.notify() 本身不会释放这个内部锁，因此，为了使等待线程在其被唤醒以后能够尽快再次获得相应的内部锁，我们应该尽可能将 Object.notify() 调用放在靠近临界区结束的地方。等待线程被唤醒之后作用处理器继续运行，如果有其他线程持有相应对象的内部锁，那么这个等待线程可能又会再次被暂停，以等待再次获得相应内部锁的机会，而这会导致上下文切换。

调用 Object.notify() 所唤醒的线程仅是相应对象上的一个任意等待线程，所以这个被唤醒线程可能不是我们真正想要唤醒的线程。因此，我们使用 `object.notifyAll()` ，它可以唤醒相应对象上的所有等待线程。

我们知道 Java 虚拟机会为每个对象维护一个入口集(Enntry Set)用于存储申请该对象内部锁的线程。此外，java  虚拟机还会为每个对象维护一个被称为等待集(Wait Set)的队列，该队列用于存储该对象上的等待线程。Object.wait() 将当前线程暂停并释放相应内部锁的同时，会将当前线程(的引用)存入该方法所述对象的等待集中。执行一个对象的 notify 方法会使该对象的等待集中的任意一个线程被唤醒，被唤醒的线程仍然停留在等待集中，直到该线程再次持有相应的内部锁的时候(此时 Object.wait()并未返回)，Object.wait() 会使当前线程从其所在的等待集中移除，接着 Object.wait() 调用就返回了。Object.wait() / notify() 实现的等待/通知中的几个关键动作，包括将当前线程加入等待集、暂停当前线程、释放锁以及唤醒后的等待线程从等待集中移除等，都是在 Object.wait() 中实现的。Object.wait() 的部分内部实现相对于如下的伪代码：
```java
public void wait() {
    // 执行线程必须持有当前对象的内部锁
    if (!Thread.holdsLock(this)) {
        throws new IllegalMonitorStateException();
    }
    if (当前对象不在等待集中) {
        // 将当前线程加入当前对象的等待集中
        addToWaitSet(Thread.currenntThread());
    }
    atomic {// 原子操作开始
        // 释放当前锁
        this.unlock();
        // 暂停当前线程
        block(Thread.currentThread()); // 语句①
    }
    // 再次申请内部锁
    acquireLock(this); // 语句②
    // 将当前线程从等待集中移除
    removeFromWaitSet(Thread.currenntThread());
    return; // 返回
}
```
Object.wait(long) 允许我们指定一个超时时间(单位为ms)，如果暂停的等待线程在这个时间内没有被唤醒，那么 Java 虚拟机自动唤醒该线程。不过，由于 Object.wait(long) 既无返回值，也不会抛异常，因此 Object.wait(long) 使用时我们需要一些额外的处理。
```java
public class TimeoutWaitExample {
    private static final Object lock = new Object();
    private static boolean ready = falsee;
    protected static final Random random = new Random();

    public static void main(String args[]) throws InterruptedException {}
}
```
Object.wait() 调用相当于 Object.wait(0) 调用。

### wait/notify 的开销及问题

过早唤醒是指保护条件没有满足的线程也被唤醒了。通过 JDK 1.5 引入的 java.util.concurrent.locks.Condition 接口可以解决过早唤醒问题。

信号丢失(Missed Signal)问题。如果等待线程在执行 Object.wait() 前，没有先判断保护条件是否成立，那么可能出现以下情形 -- 通知线程在该等待线程进入临界区之前就已经更新了相关共享变量，使得相应的保护条件处理并进行了通知，但是此时等待线程还没有被暂停，自然也无法唤醒了，之后等待线程执行 wait，这种情况被称为 信号丢失(Missed Signal)。只要将保护条件的判断和 Object.wait() 调用放在一个循环语句之中就可避免上述场景的信号丢失。

信号丢失的另一种表现是在调用 Object.notifyAll() 的地方调用了 Object.notify()。总的来说，信号丢失本质上是代码错误，不是 Java 标准库 API 自身的问题。

欺骗性唤醒(Spurious Wakeup)问题。等待线程也可能在没有其他任何线程执行 Object.notify() / Object.notifyAll() 的情况下被唤醒。这种现象被称为欺骗性唤醒。由于欺骗性唤醒的作用，等待线程被唤醒时，该线程所需的保护条件可能仍然未成立，因此此时没有任何线程对相关共享变量进行过更新。可见，欺骗性唤醒也会导致过早唤醒。欺骗性唤醒虽然在实践中出现的概率非常低，但由于操作系统是允许这种现象出现的，因此 Java 平台同样允许这种现象存在。欺骗性唤醒是 Java 平台对操作系统妥协对的一种结果。只要我们将对保护条件的判断和 Object.wait() 调用放在一个循环语句中，欺骗性唤醒就不会对我们造成实际的影响。

上下文切换问题。wait/notify的使用可能导致较多的上下文切换。

以下方法有助于避免或减少 wait/notify 导致过多的上下文切换。

- 在保证程序正确性的前提下，使用 Object.notify() 替代 Object.notifyAll() 。
- 在线程执行完 Object.notify() 后，尽快释放相应的内部锁。

### Object.notify() / Object.notifyAll() 的选用

notify 可能导致信号丢失，notifyAll()效率不太高。实现通知的一种比较流行的保守方法是优先使用notifyAll保障正确性，只有在下列条件全部满足的情况下，才能用 notify 代替 notifyAll：
- 一次通知仅需要唤醒至多一个线程；
- 相应对象的等待集中仅包含同质等待线程。所谓同质等待指的是这些线程使用同一个保护条件，并且这些线程在 Object.wait() 调用后的处理逻辑一致。最为典型的同质线程是使用同一个 Runnable 接口实例创建的不同线程或者从同一个 Thread 子类new出来的多个实例。

### wait/notify 与 Thread.join

Thread.join() 可以使当前线程等待目标线程结束后才继续运行。Thread.join() 还有另外一个声明：
```java
public final void join(long millis) throws InterruptedException;
```
join(long) 允许我们指定一个超时时间。如果目标线程没有在指定的时间终止，那么当前线程也会继续运行。join(long) 实际上就是使用了 wait/notify 来实现的。Thread.join(long) 的源码如下：
```java
public final synchronized void join(long millis) throws InterruptedException {
    long base = System.currentTimeMillis();
    long now = 0;

    if (millis < 0) {
        throw new IllegalArgumentException("timeout value is negative");
    }

    if (millis == 0) {
        while (isAlive()) {
            wait(0);
        }
    } else {
        while (isAlive()) {
            long delay = millis - now;
            if (delay <= 0) {
                break;
            }
            wait(delay);
            now = System.currentTimeMillis() - base;
        }
    }
}
```
join(long) 是一个同步方法。它检测到目标线程未结束时会调用 wait 方法来暂停当前线程，直到目标线程终止。Java 虚拟机会在目标线程的 run 方法结束后执行该线程对象的 notifyAll 方法来通知所有的等待线程。可见这里的目标线程充当了同步对象的角色，而 Java 虚拟机中 notifyAll 方法的执行线程为通知线程。

Thread.join() 调用等价于 Thread.join(0) 。

## Java 条件变量

总的来说，Object.wait()/notify() 过于底层，并且还处在过早唤醒以及 Object.wait(long) 无法区分其发挥是由于等待超时还是被通知线程唤醒等问题。但了解 wait/notify 有助于我们理解和维护现有线程以及学习和使用 JDK1.5 中引入的新的标准库类 java.util.concurrent.locks.Condition 接口。

Condition 接口可作为 wait/notify 的替代品来实现等待/通知，它为解决过早唤醒问题提供了支持，并解决了 Object.wait(long) 不能区分其返回是由于等待超时而导致的问题。

Condition 接口定义的 await 方法、signal 方法和 signalAll 方法分别相当于 Object.wait()、Object.notify() 和 Object.notifyAll() 。

Lock.newCondition() 的返回值就是一个 Condition 实例，因此调用任意一个显式锁实例的 newCondition 方法可创建一个相应的 Condition 接口。Object.wait() / notify() 要求其执行线程持有这些方法所属的内部锁，类似地，Condition.await()/signal() 也要求其执行线程持有该 Condition 实例的显式锁。Condition 实例也被称为条件变量(Condition Variable)或条件队列(Condition Queue)，每个 Condition 实例内部维护了一个用于存储等待线程的队列。设 cond1 和 cond2 是两个不同的 Condition 实例，一个线程执行 cond1.wait() 会导致其被暂停(线程声明周期变为 WAITING并传入cond1的等待队列)。cond1.signal() 会使 cond1 的等待队列中的一个任意线程被唤醒。cond1.signalAll() 会使 cond1 的等待队列中的所有线程被唤醒，而 cond2 中的等待队列中的任何一个等待线程不受影响。

Condition 接口的使用方法与 wait/notify 的使用方法相似，代码如下：

```java
class ConditionUsage {
    private final Lock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();
    public void aGuaredMethod() throws Interrupted {
        lock.lock();
        try {
            while(保护条件不成立) {
                condition.wait();
            }
            // 执行目标动作
            doAction();
        } finally {
            lock.unlock();
        }
    }
    private void doAction() {
        // ...
    }
    public void anNotifycationMethod() throws Interrupted {
        lock.lock();
        try {
            // 更新共享变量
            changeState();
            condition.signal();
        } finally {
            lock.unlock();
        }
    }
    private void changeState() {
        // ...
    }
}
```
可见，Condition.wait()/signal() 的执行线程需要持有创建相应条件变量的显式锁。对保护条件的判断，Condition.wait() 的调用也同样放在一个循环语句中，并且该循环语句与目标动作的执行放在同一个显式锁所引导的临界区中，这同样也是考虑了欺骗性唤醒问题、信号丢失问题。

Condition.await() 与 Object.wait() 类似，它使得当前线程暂停的同时，也使当前线程释放其持有的相应显式锁，并且此时 condition.await() 未返回。被唤醒的等待线程再次获得相应的显式锁后 Condition.await() 调用才返回。

Condition 接口还解决了 Object.wait(long) 存在的问题 —— Object.wait(long) 无法区分其返回是由于等待超时还是被通知的。Condition.awaitUntil(Date deadline) 可以返回带超时限制的等待，并且该方法的返回值能够区分该方法调用是由于等待超时还是被 signal/signalAll 唤醒。返回 true 表示进行的等待尚未达到最后期限。

使用条件变量实现等待超时控制：
```java
public class TimeoutWaitWithCondition {
    private static final Lock lock = new ReentrantLock();
    private static final Condition  condition = lock.newCondition();
    private static boolean ready = false;
    private static final Random random = new Random();

    public static void main(String[] args) throws InterruptedException {
        Thread t = new Thread() {
            @Override
            public void run() {
                for (;;) {
                    lock.lock();
                    try {
                        ready = random.nextInt(100) < 5 ? true : false;
                        if (ready) {
                            condition.signal();
                        }
                    } finally {
                        lock.unlock();
                    }
                    Tools.randomPause(500);
                }
            }
        };
        t.setDaemon(true);
        t.start();
        waiter(1000);
    }
    public static void waiter(final long timeOut) throws InterruptedException {
        if (timeOut < 0) {
            throw new IllegalArgumentException();
        }
        final Date deadline = new Date(System.currentTimeMillis()+timeOut);
        boolean continueToWait = true;
        lock.lock();
        try {
            while (!ready) {
                System.out.printf("still not ready, continue to wait: %s\n", continueToWait);
                if(!continueToWait){
                    System.out.println("Wait timed out, unable to executionn target action.");
                    return;
                }
                continueToWait = condition.awaitUntil(deadline);
            }
            guardedAction();
        } finally {
            lock.unlock();
        }
    }
    public static void guardedAction() {
        System.out.println("Take some action.");
    }
}
```
使用条件变量所产生的开销与 wait/notify 方法基本类似，不过由于条件变量的使用可以避免过早唤醒问题，因此其使用导致的上下文切换要比 wait/notify 少一些。

## 倒计时协调器：CountDownLatch

Thread.join 实现的是一种线程等待另一个线程结束。有时候一个线程可能只需要等待其他线程执行特定操作结束即可，不必等待这些线程终止。当然，此时我们可以用条件变量来实现。不过我们可以使用更加直接的工具类：java.util.concurrent.CountDownLatch。

CountDownLatch 可以用来实现一个(或者多个)线程等待其他线程完成一组特定的操作后才继续运行。这组操作被称为先决操作。

CountDownLatch 内部会维护一个用于表示未完成的先决操作数量的计数器。CountDownLatch 每执行一次，计数值减一。CountDownnLatch.await() 相当于一个保护方法，其保护条件为 “计数器值为0”，表示所有先决条件已经执行完毕。当计数值到达0时，唤醒相应实例上所有等待线程，之后计数值也不会再变了。
```java
public class T3 {
    public static void main(String[] args) {
        final CountDownLatch latch = new CountDownLatch(3);
        new Thread(() -> {
            System.out.println(System.currentTimeMillis() + " : thread-1 start");
            for (String opt: new String[] {"A", "B", "C", "D"}) {
                Tools.randomPause(1000);
                System.out.printf("%d : %s over\n", System.currentTimeMillis(), opt);
                latch.countDown();
            }
        }).start();
        new Thread(() -> {
            System.out.println(System.currentTimeMillis() + " : thread-2 start");
            try {
                latch.await();
                System.out.println(System.currentTimeMillis() + " : thread-2 do action");
            } catch (InterruptedException e) {
                System.out.println("thread-2 is interrupted");
            }
        }).start();
    }
}
```
执行结果为：
```
1636643712024 : thread-1 start
1636643712024 : thread-2 start
1636643712153 : A over
1636643712189 : B over
1636643712922 : C over
1636643712923 : thread-2 do action
1636643713370 : D over
```
## 栅栏(CyclicBarrier)

有时候多个线程可能需要等待对方执行到代码的某个地方(集合点)，这时这些线程才能够继续执行。1.5 开始引入 java.util.concurrent.CyclicBarrier 可以用来实现这种等待。

使用 CyclicBarrier 实现等待的线程被称为参与方，执行 CyclicBarrier.await() 即可。示例代码：
```java
public class CyclicBarrierDemo {
    public static void main(String[] args) {
        CyclicBarrier barrier = new CyclicBarrier(3, () -> {
            System.out.println(System.currentTimeMillis() + " : exec barrierAction");
        });
        for (int i = 0; i < 7; ++i) {
            new Partition(barrier, "t"+i).start();
        }
    }
    static class Partition extends Thread {
        private final CyclicBarrier barrier;
        public Partition(CyclicBarrier barrier, String threeadName) {
            super(threeadName);
            this.barrier = barrier;
        }
        @Override
        public void run() {
            Tools.randomPause(2000);
            System.out.printf("%d : %s wait...\n",System.currentTimeMillis(), getName());
            try {
                barrier.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
            System.out.printf("%d : %s done...\n",System.currentTimeMillis(), getName());
        }
    }
}
```
输出结果为：
```
1636648229126 : t3 wait...
1636648229428 : t2 wait...
1636648229432 : t1 wait...
1636648229433 : exec barrierAction
1636648229434 : t1 done...
1636648229434 : t2 done...
1636648229434 : t3 done...
1636648230060 : t5 wait...
1636648230239 : t4 wait...
1636648230604 : t0 wait...
1636648230605 : exec barrierAction
1636648230605 : t0 done...
1636648230605 : t4 done...
1636648230605 : t5 done...
1636648230920 : t6 wait...
```

## 生产者-消费者模式

LinkedBlockingQueue 适合在生产者和消费者之间的并发程度比较大的情况下使用。

ArrayBlockingQueue 适合在生产者和消费者之间的并发程度比较低的情况下使用。

SynchronousQueue 适合在消费者处理能力与生产者处理能力相差不大的情况下使用。

### 流量控制与信号量 Semaphore

java.util.concurrent.Semaphore 可以用来实现流量控制。

我们把代码所访问的特定资源或执行特定操作的机会统一看作是一种资源，这种资源被称为虚拟资源。Semaphore 相当于虚拟资源配额管理器，它可以用来控制同一时间对虚拟资源的访问次数。

为了对虚拟资源的访问进行流量控制，我们必须使相应代码只有在获得相应配额的情况下才能访问。为此，相应代码在访问虚拟资源前必须提出申请相应的配额，并在资源访问结束后返回响应的配额。Semaphore.acquire() 在成功获得一个配额后会立即返回。如果当前的可用配额不足，那么 Semaphore.acquire() 会使其执行线程暂停。Semaphore 内部会维护一个等待队列用于存储这些被暂停的线程。Semaphore.acquire() 在其返回前总是会将当前可用配额减少1。Semaphone.release() 会使当前可用配额增加1，并唤醒相应 Semaphore 等待队列中的任意等待线程。

下面是没用Semaphore的情况：
```java
public class SemaphoreDemo {
    public static void main(String[] args) {
        for (int i = 0; i < 5; ++i) {
            new Thread(() -> foo()).start();
        }
    }
    static void foo () {
        System.out.printf("%s : %s start\n", System.currentTimeMillis(),
                Thread.currentThread().getName());
        try { Thread.sleep(5000); } catch (InterruptedException e) {}
        System.out.printf("%s : %s end\n", System.currentTimeMillis(),
                Thread.currentThread().getName());
    }
}
```
输出为：
```
1636677895772 : Thread-0 start
1636677895772 : Thread-4 start
1636677895772 : Thread-3 start
1636677895772 : Thread-2 start
1636677895772 : Thread-1 start
1636677900899 : Thread-4 end
1636677900899 : Thread-1 end
1636677900899 : Thread-2 end
1636677900899 : Thread-3 end
1636677900899 : Thread-0 end
```

下面是使用了Semaphore的情况：
```java
public class SemaphoreDemo {
    static Semaphore semaphore = new Semaphore(2);
    public static void main(String[] args) {
        for (int i = 0; i < 5; ++i) {
            new Thread(() -> {try {foo();} catch (InterruptedException e) {}}).start();
        }
    }
    static void foo () throws InterruptedException {
        semaphore.acquire();
        try {
            System.out.printf("%s : %s start\n", System.currentTimeMillis(),
                    Thread.currentThread().getName());
            Thread.sleep(5000);
            System.out.printf("%s : %s end\n", System.currentTimeMillis(),
                    Thread.currentThread().getName());
        } finally {
            semaphore.release();
        }
    }
}
```
可以控制每次只有两个线程同时运行，实现限流：
```
1636678562619 : Thread-0 start
1636678562619 : Thread-1 start
1636678567641 : Thread-0 end
1636678567641 : Thread-1 end
1636678567642 : Thread-2 start
1636678567642 : Thread-3 start
1636678572648 : Thread-3 end
1636678572648 : Thread-2 end
1636678572648 : Thread-4 start
1636678577653 : Thread-4 end
```

对于阻塞队列，可以通过 take 和 put 完成阻塞（直接看BlockingQueue提供的接口即可）。

## 线程中断机制

调用 Thread 实例的 interrupt 方法将导致线程中断。调用 Thread.interrupted() 将导致当前线程的中断标志取消。调用 Thread 实例的 isInterrupted 方法判断中断事件是否已经处理。

## 线程停止

某些情况下，我们可能需要主动停止线程而不是等待线程自然终止(run方法返回)。一些典型场景如下：
- 服务或系统关闭。当一个服务不再被需要的时候，我们应该及时停止该服务所启动的工作者线程以节约宝贵的线程资源。由于非守护线程(用户线程)会阻止虚拟机正常关机，因此在系统停止前所有用户线程都应该先行停止。
- 错误处理。同质工作者线程中的一个线程出现不可恢复异常时，其他线程往往也没有必要继续运行下去了，此时我们需要主动停止其他工作者线程。
- 用户取消任务。

停止线程却是目标简单，但实现并不简单的意见事情：首先，Java 标准库并没有提供可以直接停止线程的 API；其次，停止线程的时候有一些额外的细节需要考虑。

一个较通用并且能够以优雅的方式实现线程停止的方案如下：
```java
public class TerminatableTaskRunner implements TaskRunnerSpec {
    protected final BlockingQueue<Runnable> channel;
    // 线程停止标记
    protected volatile boolean inUse = true;
    // 待处理任务计数器
    public final AtomicInteger reservation = new AtomicInteger(0);
    private volatile Thread workerThread;
    public TerminatableTaskRunner(BlockingQueue<Runnable> channel) {
        this.channel = channel;
        thsi.workerThread = new WorkerThread();
    }
    public TerminatableTaskRunner() {
        this(new LinkedBlockingQueue<Runnable>());
    }
    @Override
    public void init() {
        final Thread t = workerThread;
        if (null != t) {
            t.start();
        }
    }
    @Override
    public void submit(Runnable task) throws InterruptedException {
        channel.put(task);
        reservations.incrementAndGet();  // 语句①
    }
    public void shutdown() {
        Debug.info("Shutting down service...");
        inUse = false;  // 语句②
        final Thread t = workerThread;
        if (null != t) {
            t.interrupt();  // 语句③
        }
    }

    class WorkerThread extends Thread {
        @Override
        public void run() {
            Runnable task = null;
            try {
                for (;;) {
                    // 线程不再被需要，且无待处理任务
                    if (!inUse && reservation.get() <= 0) {
                        break;
                    }
                    task = channel.take();
                    try {
                        task.run();
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    // 待处理任务数减少1
                    reservations.decrementAndGet(); // 语句⑤
                } catch (InterruptedException e) {
                    workerThread = null;
                }
                Debug.info("worker thread terminated.")
            }
        }  //WorkerThread 结束
    }
}
```

