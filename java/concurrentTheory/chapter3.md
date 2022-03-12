# Lock 与 Condition

## 互斥锁

因为在 Concurrent 包中的锁都是“可重入锁”，所以一般都命名为 ReentrantX。可重入锁是指，当一个线程调用 object.lock() 拿到锁，进入互斥区后，再次调用 object.lock()，仍然可以拿到该锁。很显然，通常的锁都要设计成可重入的，否则就会发生死锁。

第二章讲的 synchronized 关键字同样是可重入锁。

## 类的继承关系

Concurrent 包中的与互斥锁 ReentrantLock 相关类之间的继承关系：

- Lock
    - ReentrantLock
- AbstractOwnableSynchronizer
    - AbstractQueuedSynchronizer
        - `$.Sync`
            - `$.NonfairSync`
            - `$.FairSync`

Lock 是一个接口，其定义如下：
```java
public interface Lock {
    void lock();
    void lockInterruptibly() throws InterruptedException;
    boolean tryLock();
    boolean tryLock(long time, TimeUnit timeUnit) throws InterruptedException;
    void unlock();
    Condition newCondition();
}
```
常用的方法是 lock / unlock。lock() 不能被中断，对应的 lockInterruptibly() 可以被中断。

ReentrantLock 本身没有代码逻辑，实现都在其内部类 Sync 中。

```java
public class ReenrantLock implements Lock, java.io.Serializable {
    private final Sync sync;

    public void lock() {
        sync.lock();
    }

    public void unlock() {
        sync.release();
    }
}
```
### 锁的公平性与非公平性

Sync 是一个抽象类，它有两个子类：FairSync 与 NonfairSync，分别对应公平锁和非公平锁。从下面的 ReentrantLock 构造函数可以看出，会传入一个 boolean 类型的变量指定锁是公平的还是非公平的，默认的为非公平锁：
```java
public ReentrantLock() {
    sync = new NonfairSync();
}
public ReentrantLock(boolean fair) {
    sync = fair ? new FairSync() : new NonfairSync();
}
```
什么叫公平锁和非公平锁呢？先举一个现实生活中的例子，一个人去火车站售票窗口买票，发现现场有人排队，于是他排在队伍末尾，遵循先到者优先服务的规则，叫公平；如果他去了不排队，直接冲到窗口买票，叫做不公平。

对应到锁的例子，一个新的线程来了之后，看到有很多线程在排队，自己排到队伍末尾，叫公平；线程来了之后，注解去抢锁，这叫做不公平。不同于现实生活，这里默认设置的是非公平锁，其实是为了提高效率，减少线程切换。

后面会详细地通过代码来对比公平锁和非公平锁在实现上的差异。

### 锁的实现原理

Sync 的 父类 AbstractQueuedSynchronizer 经常被称为队列同步器(AQS)，这个类非常关键，下面会反复提到，该类的父类是 AbstractOwnableSynchronizer。

上一章讲的 Atomic 类都是“自旋”性质的锁，而本章讲的锁嫁给你具备 synchronized 功能，也就是可以阻塞一个线程。为了实现一把具有阻塞或唤醒功能的锁，需要几个核心要素。

1. 需要一个 state 变量，标记该锁的状态。state 变量至少有两个值：0、1.对 state 变量的操作，要确保线程安全，也就是会用到 CAS。
2. 需要记录当前是哪个线程持有锁；
3. 需要底层支持对一个线程进行阻塞或唤醒操作；
4. 需要有一个队列维护所有阻塞的线程。这个队列也必须是线程安全的无锁队列，也需要用到 CAS。

针对 1、2，在上面两个类中有相应的体现：
```java
public abstract class AbstractOwnableSynchronizer implements java.io.Serializable {
    /**
     * The current owner of exclusive mode synchronization.
     * 记录锁被哪个线程持有
     */
    private transient Thread exclusiveOwnerThread;
}
public abstract class AbstractQueuedSynchronizer
    extends AbstractOwnableSynchronizer
    implements java.io.Serializable {
    /**
     * The synchronization state.
     * 记录锁的状态，通过 CAS 修改 state 值
     */
    private volatile int state;
}
```
state 取值不仅可以是0、1，还可以大于1，就是为了支持锁的可重入性。例如，同一个线程，调用 5 次 lock，state 会变为 5，然后调用 5 次unlock，state 减为 0.

当 state = 0 时，没有线程持有锁，exclusiveOwnerThread = null；
当 state = 1 时，有一个线程持有锁，exclusiveOwnerThread = 该线程；
当 state > 1 时，说明该线程重入了该锁。

针对要素3，在 Unsafe 类中，提供了阻塞或者唤醒的一对操作原语，也就是 park / unpark。
```java
public native void unpark(Object var1);
public native void park(boolean var1, long var2);
```
有一个 LockSupport 的工具类，对这一对原语做了简单封装：
```java
public class LockSupport {
    ...
    public static void park() {
        UNSAFE.park(false, 0L);
    }
    public static void unpark(Thread thread) {
        if (thread != null)
            UNSAFE.unpark(thread);
    }
}
```
在当前线程中，调用 park()，该线程会被阻塞；在另一个线程中调用 unpark，传入一个被阻塞的线程，就可以唤醒阻塞在 park() 位置的线程。

尤其是 unpark(Thread t)，它实现了一个线程对另一个线程的“精准唤醒”。前面讲到的 wait()/notify()，notify() 也只是唤醒某一个线程，但无法指定唤醒哪个线程。

针对要素4，在 AQS 中利用双向链表和 CAS 实现了一个阻塞队列。如下所示：
```java
public abstract class AbstractQueuedSynchronizer{
    ...
    static final class Node {
        volatile Node prev;
        volatile Node next;
        volatile Thread thread;  // 每个 Node 对应一个被阻塞的线程
        ...
    }
    private transient volatile Node head;
    private transient volatile Node tail;
}
```
阻塞队列是整个 AQS 核心中的核心，下面做进一步的阐述。入队就是把新的 Node 加到 tail 后面，然后对 tail 进行 CAS 操作；出队就是对 head 进行 CAS 操作，把 head 向后移一个位置。

入队列就是把新的 Node 加到 tail 后面，然后 对 tail 进行 CAS 操作，逻辑为：
```java
    private Node enq(final Node node) {
        for (;;) {
            Node t = tail;
            if (t == null) { // Must initialize
                if (compareAndSetHead(new Node()))
                    tail = head;
            } else {
                node.prev = t;
                if (compareAndSetTail(t, node)) {
                    t.next = node;
                    return t;
                }
            }
        }
    }
```

### 公平与非公平的 lock 的实现差异

下面分析基于 AQS，ReentrantLock 在公平性和非公平上的实现差异：
```java
    static final class NonfairSync extends Sync {
        ...
        final void lock() {
            if (compareAndSetState(0, 1))
                setExclusiveOwnerThread(Thread.currentThread());
            else
                acquire(1);
        }
        ...
    }

    static final class FairSync extends Sync {
        ...
        final void lock() {
            acquire(1);
        }
        ...
    }
```
acquire() 是 AQS 的一个模板方法，如下所示：
```java
    public final void acquire(int arg) {
        if (!tryAcquire(arg) &&
            acquireQueued(addWaiter(Node.EXCLUSIVE), arg))
            selfInterrupt();
    }
```
tryAcquire(...)是一个虚函数，也就是再次尝试拿锁，被 NonfairSync 与 FairSync 分别实现。acquireQueued(...)函数的目的是将线程放入阻塞队列，然后阻塞该线程。

下面再来看看 FairSync 与 NonfairSync 的 tryAcquire(1) 的区别。

```java
final boolean nonfairTryAcquire(int acquires) {
    final Thread current = Thread.currentThread();
    int c = getState();
    if (c == 0) {
        if (compareAndSetState(0, acquires)) {
            setExclusiveOwnerThread(current);
            return true;
        }
    }
    else if (current == getExclusiveOwnerThread()) {
        int nextc = c + acquires;
        if (nextc < 0) // overflow
            throw new Error("Maximum lock count exceeded");
        setState(nextc);
        return true;
    }
    return false;
}
```
```java
    static final class FairSync extends Sync {
        ...
        protected final boolean tryAcquire(int acquires) {
            final Thread current = Thread.currentThread();
            int c = getState();
            if (c == 0) {
                if (!hasQueuedPredecessors() &&
                    compareAndSetState(0, acquires)) {
                    setExclusiveOwnerThread(current);
                    return true;
                }
            }
            else if (current == getExclusiveOwnerThread()) {
                int nextc = c + acquires;
                if (nextc < 0)
                    throw new Error("Maximum lock count exceeded");
                setState(nextc);
                return true;
            }
            return false;
        }
    }
```
两段代码非常相似，唯一的区别是第二段代码多了一个 `if (!hasQueuedPredecessors())`。就是只有当 c==0（没有线程持有锁），并且排队在第一个时（即当前队列中没有其他线程的时候），才去抢锁，否则继续排队。

### 阻塞队列与唤醒机制

下面进入锁最为关键的部分，即 acquireQueue() 函数内部一探究竟。
```java
    public final void acquire(int arg) {
        if (!tryAcquire(arg) &&
            acquireQueued(addWaiter(Node.EXCLUSIVE), arg))
            selfInterrupt();  // 如果发生过中断，则进行补偿
    }
```
先说 addWaiter 函数，就是为当前线程生成一个 Node，然后把 Node 放入双向队列的尾部。要注意的是，这只是把 Thread 对象放入一个队列中而已，线程本身并未阻塞。
```java
    private Node addWaiter(Node mode) {
        Node node = new Node(Thread.currentThread(), mode);
        // Try the fast path of enq; backup to full enq on failure
        Node pred = tail;
        if (pred != null) {
            node.prev = pred;
            if (compareAndSetTail(pred, node)) {
                pred.next = node;
                return node;
            }
        }
        enq(node);
        return node;
    }
```
在 addWaiter 函数把 Thread 对象加入阻塞队列之后，要靠 acquireQueued() 函数完成。线程一旦进入 acquireQueued() 就会被无限期阻塞，即使有其他线程调用了 interrupt 函数，也不能将其唤醒，除非有其他线程释放了锁，并且该线程拿到了锁，才会从 acquireQueued返回。

```java
    final boolean acquireQueued(final Node node, int arg) {
        boolean failed = true;
        try {
            boolean interrupted = false;
            for (;;) {
                final Node p = node.predecessor();
                if (p == head && tryAcquire(arg)) {
                    setHead(node);
                    p.next = null; // help GC
                    failed = false;
                    return interrupted;
                }
                if (shouldParkAfterFailedAcquire(p, node) &&
                    parkAndCheckInterrupt())
                    interrupted = true;
            }
        } finally {
            if (failed)
                cancelAcquire(node);
        }
    }
```
acquireQueued 函数有一个返回值，该函数虽然不会中断响应，但它会记录被阻塞期间有没有其他线程向他发送过中断信号，如果有，则该函数会返回 true；否则返回 false。

阻塞就发生在下面这个函数中：
```java
    private final boolean parkAndCheckInterrupt() {
        LockSupport.park(this);
        return Thread.interrupted();
    }
```
线程调用 park 函数将自己阻塞起来，直到被其他线程唤醒，该函数返回。park 函数返回有两种情况。

1. 其他线程调用了 unpark(Thread t);
2. 其他线程调用了 t.interrupt()。要注意的是，lock() 不能响应中断，但是 LockSupport.park() 会响应中断。

正因为 LockSupport.park() 可能被中断唤醒，acquireQueued 函数才写了一个 for 死循环。唤醒之后，如果发现自己排在队列头部，就去拿锁，如果拿不到锁，则再次阻塞自己，不断重复此过程，直到拿到锁。

被唤醒后，通过 Thread.interrupted() 判断是否被中断唤醒。如果是1，会返回false，如果是2，返回true。

## 读写锁

ReentrantReadWriteLock 实现了 ReadWriteLock 接口。使用方式如下：
```java
ReadWriteLock rwLock = new ReentrantReadWriteLock();
Lock rLock = rwLock.readLock();
rLock.lock();
rLock.unlock();
Lock wLock = rwLock.writeLock();
wLock.lock();
wlock.unlock();
```
也就是说，当使用 ReadWriteLock 的时候，并不是直接使用，而是获得其内部的读锁和写锁，然后分别调用 lock/unlock。

### 读写锁实现的基本原理

表面上看，ReadLock 和 WriteLock 是两把锁，实际上它只是同一把锁的两个视图而已。什么叫两个视图呢？可以理解为是一把锁，线程分成两类：读线程和写线程。读线程和读线程之间不互斥，读线程和写线程互斥，写线程和写线程也互斥。

从下面的构造函数可以看出，readerLock 和 writerLock 实际共用一个 sync 对象。sync 对象同互斥锁一样，分为非公平和公平两种策略，并继承自 AQS。
```java
    public ReentrantReadWriteLock() {
        this(false);
    }

    public ReentrantReadWriteLock(boolean fair) {
        sync = fair ? new FairSync() : new NonfairSync();
        readerLock = new ReadLock(this);
        writerLock = new WriteLock(this);
    }
```
同互斥锁一样，读写锁也使用 state 变量表示锁的状态。只是 state 变量在这里的含义与互斥锁完全不同。在 内部类 Sync 中，对 state 变量进行了重新定义：
```java
    abstract static class Sync extends AbstractQueuedSynchronizer {

        static final int SHARED_SHIFT   = 16;
        static final int SHARED_UNIT    = (1 << SHARED_SHIFT);
        static final int MAX_COUNT      = (1 << SHARED_SHIFT) - 1;
        static final int EXCLUSIVE_MASK = (1 << SHARED_SHIFT) - 1;

        // 持有读锁的线程的重入次数
        static int sharedCount(int c)    { return c >>> SHARED_SHIFT; }
        // 持有写锁的线程的重入次数
        static int exclusiveCount(int c) { return c & EXCLUSIVE_MASK; }
    }
```
也就是将 state 变量拆分成两半，低16位记录写锁。高16位记录读锁。

这个地方的设计很巧妙，为什么把一个 int 类型拆分两半，而不是用两个 int 型变量分别表示读锁和写锁的状态呢？这是因为无法用一次 CAS 同时操作两个 int 变量，所以用了一个 int 型的高16位和低16位分别表示读锁和写锁的状态。
