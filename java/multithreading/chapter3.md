# 线程同步机制

## 线程同步机制简介

从应用程序的角度来看，线程安全问题的产生是由于多线程应用程序缺乏某种东西 -- 线程同步机制。

线程同步机制是一套用来协调线程间的数据访问(Data Access)及活动(Activity)的机制，该机制用于保障线程安全以及实现这些线程的共同目标。

从广义上说，Java 平台提供的线程同步机制包括锁、volatile 关键字、final 关键字、static 关键字以及一些相关的API，如 Object.wait()/Object.notify() 等。本章介绍用于协调线程间共享数据访问的相关关键字和API，有关协调线程间活动的相关API在第5章介绍。

## 锁概述

线程安全问题产生的前提是多个线程并发访问共享变量、共享资源(统称共享数据)。于是，保证线程安全的一种方法：将多个线程对共享数据的并发访问转换为串行访问。锁(Lock)就是利用这种思路以保障线程安全的线程同步机制。

按照上述思路，锁可以理解为对共享数据进行保护的许可证。对于同一个许可证所保护的共享数据而言，任何线程访问这些共享数据之前都必须先持有该许可证。

一个线程在访问共享数据前必须申请相应的锁，线程的这个动作被称为锁的获得(Acquire)。一个线程获得某个锁，我们就称该线程为相应锁的持有线程。

锁的持有线程在其获得锁之后和释放锁之前这段时间内锁执行的代码称为临界区(Critical Section)。因此，共享数据只允许在临界区内进行访问，临界区一次只能被一个线程执行。

如果有多个线程访问同一个锁所保护的共享数据，那么我们称这些线程同步在这个锁上，或者称我们对这些线程所进行的共享数据访问进行了加锁。相应地，这些线程所执行的临界区被称为这个锁所引导的临界区。

锁有排他性(Exclusive)，即一个锁一次只能被一个线程持有。因此这种锁称为排他锁或互斥锁(mutex).

Java 虚拟机对锁的实现方式划分，Java 平台中的锁包括内部锁(Intrinsic Lock)和显式锁(Explicit Lock)。内部锁是通过 synchronized 关键字实现的；显式锁是通过 java.concurrent.locks.Lock 接口的实现类(如 java.concurrent.locks.ReentrantLock 类)实现的。

### 锁的作用

锁能够保护共享数据以实现线程安全，其作用包括保障原子性、保障可见性和保障有序性。

在 Java 平台中，锁的获得隐含着刷新处理器缓存这个动作，这使得读线程在执行临界区代码前（获得锁之后）可以将写线程对共享变量所做的更新同步到该线程执行处理器的高速缓存中；而锁的释放隐含着冲刷处理器缓存这个动作，这使得写线程对共享变量所做的更新能够被“推送”到该线程执行处理器的高速缓存中，从而对读线程可同步。由此保证可见性。

在理解以及使用锁保证线程安全的时候，需要注意锁对可见性、原子性和有序性的保障是有条件的，我们要同时保证以下两点得以满足：

- 这些线程在访问同一组共享数据的时候必须使用同一个锁；
- 这些线程中的任意一个线程，即使其仅仅是读取这组共享数据而没有对其进更改，也需要在读取时持有相应的锁；

### 与锁相关的几个概念

#### 可重入性

可重入性(Reentrancy)描述这样一个问题：一个线程在其持有一个锁的时候能否再次(或多次)申请该锁。如果一个线程持有一个锁的时候还能够继续成功申请该锁，那么我们就称该锁是可重入的(Reentrant)，否则我们就称该锁是不可重入的(Non-reentrant)。可重入性问题的由来可以通过如下伪代码理解：
```java
void methodA() {
    acquireLock(lock);  // 申请锁

    ... ...
    methodB();
    releaseLock(lock);
}
void methodB() {
    acquireLock(lock);
    ... ...
    releaseLock(lock);
}
```
方法 methodA 使用了锁 lock，该锁引导的临界区代码又调用了另外一个方法 methodB，而该方法也使用了 lock。此时，methodB 能否获得锁就是锁的可重入性问题。

#### 锁的争用与调度

锁可以被看作多线程程序访问共享数据时所需持有的一种排他性资源。因此，资源的争用、调度的概念对锁也适用。Java 平台中锁的调度策略也包括公平策略和非公平策略，相应的锁就称为公平锁和非公平锁。内部锁属于非公平锁，而显式所即支持公平锁，又支持非公平锁。

#### 锁的粒度

一个锁实例可以保护一个或多个共享数据。一个锁实例保护的共享数据的数量大小被称为该锁的粒度(Granularity)，保护的共享数据数量大，则称该锁粒度粗，否则称该锁粒度细。锁粒度过粗会导致线程在申请锁的时候需要进行不必要的等待，过细会增加锁调度的开销。

### 锁的开销及可能导致的问题

锁的开销包括锁的申请和释放所产生的开销，以及锁可能导致的上下文切换的开销。这些开销主要是处理器时间。如果线程要获取一个已经被争用的锁，就会导致上下文切换。

此外，锁的不正确使用也会导致一些线程活性故障：
- 锁泄露（Lock Leak）：锁泄露是指一个线程获得某个锁之后，由于程序的错误，缺陷导致该锁无法被释放，导致其他线程一直无法获得该锁的现象。锁泄露不易发现。
- 锁对的不正确使用还可能导致死锁、锁死等线程活性故障。

## 内部锁：synchronized 关键字

Java 平台中的任何一个对象都有唯一一个与之关联的锁。这种锁被称为监视器(Monitor)或者内部锁(Intrinsic Lock)。内部锁是一种排它锁，它能够保障原子性、可见性和有序性。

内部锁是通过 synchronized 关键字实现的。synchronized 关键字可以用来修饰方法以及代码块(`{   }` 包裹的代码)。

synchronized 关键字修饰的方法被称为同步方法(Synchronized Method)。synchronized 修饰的静态方法就被成为同步静态方法，synchronized 修饰的实例方法就被称为同步实例方法。同步方法的整个方法体就是一个临界区。

synchronized 关键字修饰的代码块称为同步块(Synchronized Block)，语法为：
```java
synchronized (锁句柄) {
    // 此代码块访问的共享数据
}
```

synchronized 关键字所引导的代码块就是临界区。锁句柄是一个对象的引用(或者能够返回对象的表达式)。例如，锁句柄可以填写为 this。习惯上我们也直接称锁句柄为锁。锁句柄对应的监视器被称为相应同步块的引导锁。相应地，我们称呼相应的同步块为该锁引导的同步块。

同步实例方法相当于已 “this” 为引导锁的同步块。因此：
```java
public synchronized short nextSequence() {
    if (sequence >= 999) {
        sequence = 0;
    } else {
        sequence++;
    }
    return sequence;
}
```
等价于：
```java
public short nextSequence() {
    synchronized {
        if (sequence >= 999) {
            sequence = 0;
        } else {
            sequence++;
        }
        return sequence;
    }
}
```
作为锁句柄的变量通常采用 final 修饰，这是因为锁句柄变量的值一旦改变，会导致执行同一个同步块的多个线程实际上使用不同的锁，从而导致竞态。因此，通常我们会使用 private 修饰作为锁句柄的变量。

注意：作为锁句柄的变量，通常采用 `private final` 修饰，如 `private final Object lock = new Object();`。

同步静态方法相当于已当前类对象作为引导锁的同步块，例如：
```java
public class SynchronizedMethodExample {
    public static synchronized void staticMethod() {
        ... ...
    }
}
```
等价于：
```java
public class SynchronizedMethodExample {
    public static void staticMethod() {
        synchronized (SynchronizedMethodExample.class) {
            ... ...
        }
    }
}
```
线程在执行临界区代码的时候，必须持有该临界区的引导锁。一个线程执行到同步块时，必须先申请该同步锁的引导锁，只有申请成功该锁的线程才能执行相应的临界区。一个线程执行完临界区代码后引导该临界区的锁就会被自动释放。在这个过程中，线程对内部锁的申请与释放动作由java虚拟机负责代为实施，这也正是 synchronized 实现的锁被称为内部锁的原因。

内部锁的的使用并不会导致锁泄漏。这是因为java编译器(javac)在将同步块代码编译为字节码的时候，对临界区中可能抛出的而程序代码中又未捕获的异常进行了特殊处理，这使得临界区的代码即使抛出异常也不会妨碍内部锁的释放。

#### 内部锁的调度

Java 虚拟机会为每个内部锁分配一个入口集合(Entry set)，用于记录等待获得相应内部锁的线程。多个线程申请同一个锁的时候，只有一个申请者能够称为该锁的持有线程，而其他申请者的申请操作会失败。这些申请失败的线程不会抛出异常，而是会被暂停(生命周期变为 BLOCKED)并被存入相应锁的入口集合中等待再次申请锁的机会。

入口集中的线程被称为相应内部锁的等待线程，当这些线程申请的锁被其他持有线程释放时，该锁的入口集中的任意线程会被 Java 虚拟机唤醒，从而得到再次申请的锁的机会。

Java虚拟机对内部锁的调度仅支持非公平调度，是可重入锁。

## 显式锁：Lock 接口

显式锁是自 JDK 1.5 开始引入的排他锁。作为一种线程同步机制，其作用与内部锁相同。它提供了一些内部锁所不具备的特性，但并不是内部锁的替代品。

显式锁(Explicit Lock)是 java.util.concurrent.locks.Lock 的实例。该接口对显式锁进行了抽象，其定义的方法如下，java.util.concurrent.locks.ReentrantLock 是 Lock 接口的默认实现：
- `void lock()`: 获取锁；
- `void unlock()`: 释放锁；
- `void lockInterruptibly()`: 如果当前线程未被中断，则获取锁；
- `Condition newCondition()`: 返回绑定到此 Lock 实例的新 Condition 实例；
- `boolean tryLock()`: 仅在调用时锁为空闲状态才获取该锁；
- `boolean tryLock(long time, TimeUnit unit)`: 如果锁在给定的等待时间内空闲，并且当前线程未被中断，则获取锁。

一个 Lock 接口实例就是一个显式锁对象，Lock 接口定义的 lock 方法和 unlock 方法分别用于申请和释放相应的 Lock 实例表示的锁。显式锁的使用方法如下所示：
```java
private final Lock lock = ...;

... ...

lock.lock();
try {
    // 在此对共享数据进行访问
    ... ...
} finally {
    // 总是在 finally 块中释放锁，以避免锁泄露
    lock.unlock();
}
```
显式锁的使用包括以下几个方面：
- 创建 Lock 接口的实例。如果没有特别的要求，我们可以创建 Lock 接口的默认实现类 ReentrantLock 的实例作为显式锁使用。
- 在访问共享数据前申请显式锁。
- 在临界区中访问共享数据。Lock.lock() 调用与 Lock.unlock() 调用之间的代码区域为临界区。不过，一般我们视上述的 try 代码块为临界区。因此，对共享数据的访问都放在该代码块中。
- 共享数据访问结束后释放锁。虽然释放锁的操作通难过调用 Lock.unlock() 即可实现，但是为了避免锁泄露，我们必须将这个调用放在 finally 块中执行。这样，无论是临界区代码执行正常还是由于其抛出异常而提前退出，相应锁的 unlock 方法总能被执行，从而避免了锁泄露问题。

循环递增序列号生成器用显式锁改写为：
```java
public class LockbasedCircularSeqGenerator implement CircularSeqGennnerator {
    private short sequence = -1;
    private Lock lock = new ReentrantLock();

    @Override
    public short nextSequence() {
        lock.lock();
        try {
            if (sequence >= 999) {
                sequence = 0;
            } else {
                sequence++;
            }
            return sequence;
        } finally {
            lock.unlock();
        }
    }
}
```

### 显式锁的调度

ReentrantLock 即支持非公平锁也支持公平锁。ReentrantLock 的一个构造器的签名为：`ReentrantLock(boolean fair)`。该构造器使我们在创建显式锁实例的时候可以指定相应的锁是否为公平锁。

公平锁保障锁调度的公平性往往是以增加了线程的暂停和唤醒的可能性，即增加了上下文切换为代价的。因此，公平锁适合于锁被持有的时间相对长或者线程申请锁的平均间隔时间相对长的情形。总的来说，使用公平锁的开销要比使用非公平锁的开销要大，因此显示锁默认使用非公平调度策略。

### 显式锁与内部锁的比较

内部锁是基于代码块的锁，因此其使用基本无灵活性可言：要么使用它，要么不使用它，别无他选。显式锁是基于对象的锁，其使用可以充分发挥面向对象编程的灵活性。比如，内部锁的申请与释放只能是在一个方法内进行，而显式锁支持在一个方法内申请锁，在另一个方法中释放锁。

内部锁基于代码块的特性有一个优势：简单易用，不会导致锁泄漏。而显式锁容易被错用而导致锁泄漏，因此使用显式锁的时候必须注意将锁的释放操作放在 finally 块中。

如果一个内部锁持有线程一直不释放该锁，那么同步在该锁上的所有线程会被暂停而使其任务无法进展。显式锁则可以轻松避免这样的问题。Lock 接口定义了一个 tryLock 方法，该方法的作用是尝试申请相应 Lock 实例锁表示的锁，如果相应的锁未被其他任何线程持有，那么该方法会返回 true，表示其获得了相应的锁；否则，该方法不会导致其执行的线程被暂停，而是直接返回 false，表示其未获得相应的锁。tryLock 方法的使用如下代码模板所示：
```java
Lock lock = ...;
if (lock.tryLock()) {
    try {
        // 在此访问共享数据
    } finally {
        lock.unlock();
    }
} else {
    // 执行其他操作
}
```
tryLock 方法是个多载(Overload)的方法，它还有另外一个签名版本：`boolean tryLock(long time, TimeUnit unit)`。

该版本的 tryLock 方法指定一个时间，如果当前线程没有在指定的时间内成功申请到相应的锁，那么 tryLock 方法直接返回 false。

在锁的调度方面，内部锁仅支持公平锁；而显式锁即支持公平锁，又支持非公平锁。

在问题定位方面，尤其是定位生产环境上的问题的时候，线程转储(Thread dump)就像是线程的工作报告一样可以告诉我们 Java 虚拟机中关于线程的详细信息。线程转储中会包含内部锁的相关信息，包括一个线程等待哪些锁以及这些锁的当前持有线程。JDK1.5下，线程转储并不包含显式锁的相关信息。JDK1.6提供的根据，jstack 所产生的线程转储中可以包含显示锁的相关信息。

```java
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ExplicitLockInfo {
    private static final Lock lock = new ReentrantLock();
    private static int sharedData = 0;

    public static void main(String[] args) throws Exception{
        Thread t = new Thread(() -> {
            lock.lock();
            try {
                try {
                    Thread.sleep(2200000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } 
            } finally {
                lock.unlock();
            }
        });
        t.start();
        Thread.sleep(100);
        lock.lock();
        try {
            System.out.println("sharedData: " + sharedData);
        } finally {
            lock.unlock();
        }
    }
}
```
跑起来后，通过 jps (或者 ps)获取进程 id，然后通过 `jstack -l 进程id` 获取转储文件，可以得到类似如下的线程信息：
```java
2021-11-03 07:32:55
Full thread dump Java HotSpot(TM) 64-Bit Server VM (25.281-b09 mixed mode):

"Attach Listener" #19 daemon prio=9 os_prio=31 tid=0x00007fa3eb029800 nid=0x4107 waiting on condition [0x0000000000000000]
   java.lang.Thread.State: RUNNABLE

   Locked ownable synchronizers:
    - None

"Thread-0" #18 prio=5 os_prio=31 tid=0x00007fa3f1855800 nid=0x5f03 waiting on condition [0x000070000a1a6000]
   java.lang.Thread.State: TIMED_WAITING (sleeping)
    at java.lang.Thread.sleep(Native Method)
    at ExplicitLockInfo.lambda$main$0(ExplicitLockInfo.java:13)
    at ExplicitLockInfo$$Lambda$1/1406718218.run(Unknown Source)
    at java.lang.Thread.run(Thread.java:748)

   Locked ownable synchronizers:
    - <0x000000076ab7aae0> (a java.util.concurrent.locks.ReentrantLock$NonfairSync)
    
    ... ...

"main" #1 prio=5 os_prio=31 tid=0x00007fa3ed009000 nid=0xe03 waiting on condition [0x0000700008040000]
   java.lang.Thread.State: WAITING (parking)
    at sun.misc.Unsafe.park(Native Method)
    - parking to wait for  <0x000000076ab7aae0> (a java.util.concurrent.locks.ReentrantLock$NonfairSync)
    at java.util.concurrent.locks.LockSupport.park(LockSupport.java:175)
    at java.util.concurrent.locks.AbstractQueuedSynchronizer.parkAndCheckInterrupt(AbstractQueuedSynchronizer.java:836)
    at java.util.concurrent.locks.AbstractQueuedSynchronizer.acquireQueued(AbstractQueuedSynchronizer.java:870)
    at java.util.concurrent.locks.AbstractQueuedSynchronizer.acquire(AbstractQueuedSynchronizer.java:1199)
    at java.util.concurrent.locks.ReentrantLock$NonfairSync.lock(ReentrantLock.java:209)
    at java.util.concurrent.locks.ReentrantLock.lock(ReentrantLock.java:285)
    at ExplicitLockInfo.main(ExplicitLockInfo.java:23)

   Locked ownable synchronizers:
    - None

    ... ...
```
可见，main 线程由于需要获得唯一标识为 0x000000076ab7aae0 的显式锁而处于等待状态，这个锁被线程 Thread-0 所持有。

显式锁提供了一些接口可以用于对锁的相关信息进行监控，而内部锁不支持这种特性。ReentrantLock 中定义的方法 isLocked() 可用于检测相应锁是否被某个线程持有，getQueueLength() 可用于检查相应锁的等待线程的数量。

内部锁的优点是简单易用，显式锁的优点是功能强大，这两种锁各自都存在一些弱势。

### 改进型锁：读写锁

锁的排他性使得多个线程无法以线程安全的方式在同一时刻对共享变量进行读取(只读取，不更新)，这不利于提高系统的并发性。对同步在同一锁之上的线程而言，对共享变量仅进行读取而没进行更新的线程称为读线程。对共享变量进行更新的线程称为写线程。

读写锁(Read/Write Lock)是一种改进型的排它锁，也被称为共享/排他(Shared/Exclusive)锁。读写锁允许多个线程同时读取共享变量，但是只允许一个线程对共享变量进行更新。

读线程在访问共享变量的时候必须持有读写锁的读锁，读锁是共享的。写线程在访问共享变量时必须持有写锁，写锁是排他的。

java.util.concurrent.locks.ReadWriteLock 接口是对读写锁的抽象，其默认实现为 java.util.concurrent.lock.ReentrantReadWriteLock，该接口定义了两个方法：`readLock` 和 `writeLock`，分别返回相应读写锁实例的读锁和写锁。这两个方法返回的值类型都是 Lock，这并不表示一个 ReadWriteLock 接口实例对应两个锁，而是代表一个 ReadWriteLock 接口实例可以充当两种角色。

读写锁使用方法：
```java
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReadWriteLockUsage {
    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();
    private final Lock readLock = rwLock.readLock();
    private final Lock writeLock = rwLock.writeLock();
    // 读线程执行该方法
    public void reader() {
        readLock.lock();
        try {
            // 在此区域内读取共享变量
        } finally {
            readLock.unlock();
        }
    }
    // 写线程执行该方法
    public void writer() {
        writeLock.lock();
        try {
            // 在此区域内读、写共享变量
        } finally {
            writeLock.unlock();
        }
    }
}
```
由于读写锁内部实现比内部锁和其他显式锁要复杂的多，因此读写锁适合于在以下条件同时得以满足的场景使用：
- 只读操作比写操作要频繁得多；
- 读线程持有锁的时间比较长；

只有同时满足以上两个条件时，读写锁才是适宜的选择，否则，读写锁会得不偿失。

ReentrantReadWriteLock 所实现的读写锁是个可重入锁。ReentrantReadWriteLock 支持锁的降级(Downgrade)，即一个线程持有读写锁的写锁的情况下，可以继续获得相应的读锁。

锁的降级的反面试锁的升级(Upgrade)，即一个线程持有读写锁的读锁的情况下，申请相应的写锁。ReentrantReadWriteLock 不支持锁的升级。读线程如果要申请写锁，必须先释放读锁，然后在申请相应的写锁。

例子：
```java
import java.util.Date;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReadWriteLockUsage {
    private final ReadWriteLock rwLock = new ReentrantReadWriteLock(true);
    private final Lock readLock = rwLock.readLock();
    private final Lock writeLock = rwLock.writeLock();
    
    public static void main(String[] args) throws Exception{
        ReadWriteLockUsage demo = new ReadWriteLockUsage();
        new Thread(() -> demo.read()).start();  // ①
        sleep(50);
        new Thread(() -> demo.read()).start();  // ②
        sleep(50);
        new Thread(() -> demo.write()).start();  // ③
    }

    static void sleep(int ms) { try { Thread.sleep(ms);} catch (Exception e) {} }

    void read() {
        readLock.lock();
        try {
            System.out.println(new Date().getTime() + " : read"); sleep(1000);
        } finally{
            readLock.unlock();
        }
    }

    void write() {
        writeLock.lock();
        try {
            System.out.println(new Date().getTime() + " : write"); sleep(1000);
        } finally{
            writeLock.unlock();
        }
    }
}
```
输出为：
```
1635901097905 : read
1635901097959 : read
1635901098961 : write
```
如果线程启动实现变为 W W R，则输出：
```
1635901192133 : write
1635901193137 : write
1635901194142 : read
```
## 锁的适用场景

锁是 Java 线程同步机制中功能最强大、适用范围最广泛，同时也是开销最大、可能导致的问题最多的同步机制。多个线程共享同一组数据的时候，如果其中有线程涉及如下操作，可以考虑用锁：

- check-then-act 操作：一个线程读取共享数据后决定下一个操作是什么；
- read-modify-write 操作
- 多个线程对多个共享数据进行更新

## 轻量级同步机制：volatile 关键字

volatile 字面含义为“易挥发”的，引申为不稳定的意思。volatile 关键字用于修饰共享可变变量，即没有使用 final 关键字修饰的实例变量或静态变量，相应的变量就被称为 volatile 变量。

volatile 表示被修饰的变量值容易改变(即被其他线程改变)，因此不稳定。volatile 变量的不稳定性意味着对这种变量的读写操作必须从高速缓存或主存中读取，以获取相对新值。因此，volatile 变量不会被编译器分配到寄存器进行存储，对 volatile 变量的读写操作都是内存访问操作。

volatile 被称为轻量级锁，作用与锁有相同的地方：保证可见性和有序性。不同的是，在原子性方面，它仅保证写 volatile 变量操作的原子性，但没有锁的排他性；其次，volatile 不会引起上下文切换。因此，volatile 更像是一个轻量级简易(功能比锁有限)的锁。

### volatile 的作用

volatile 的作用包括：保证可见性、有序性和 long/double 型变量读写的原子性。

访问同一个 volatile 变量的线程被称为同步在这个变量之上的线程，其中读取这个变量的被称为读线程，更新这个变量的线程被称为写线程。一个线程可以既是读线程，又是写线程。

volatile 也可以看作 JIT 编译器的一个提示，它相当于告诉 JIT 编译器相应变量的值可能被其他处理器更改，从而使 JIT 编译器不会对相应代码作出一些优化而导致可见性问题。

如果 volatile 修饰的是一个数组，那么 volatile 只能够对数组引用本身的操作起作用，无法对数组元素的操作起作用。即 `volatile int[] a = {1,2,3}` ，则 `int b = a[0]` 不能保证一定是最新值。如果要对数组元素的读、写操作也能触发 volatile 关键字的作用，那么我们可以使用 AtomicIntegerArray、AtomicLongArray 和 AutomicReferenceArray。

### volatile 的开销

volatile 变量的开销包括读变量和写变量两个方面。volatile 变量的读、写不会导致上下文切换，因此 volatile 的开销要比锁小。但比普通变量要高一些。

### volatile 的典型应用场景与实战案例

volatile 除了用于保障 long/double 型变量的读、写操作的原子性，其典型使用场景还包括以下几个方面：

场景一：由 volatile 变量作为状态标志。在该场景中，应用程序的某个状态由一个线程设置，其他线程会读取该状态并以该状态作为其计算的依据。此时，使用你那个volatile的好处是一个线程能够通知另一个线程某种时间的发生，而这些线程又无需因此加锁，从而避免锁的开销以及相关问题。

场景二：使用 volatile 保障可见性。

场景三：使用 volatile 变量代替锁，volatile 关键字并非锁的替代品，但是在一定的条件下它比锁更合适（开销小，代码简单）。多个线程共享一组可变状态变量的时候，通常我们需要使用锁来保障对这些变量的更新操作的原子性，以避免产生数据不一致问题。利用 volatile 变量写操作具有的原子性，我们可以把这一组可变状态变量封装成一个对象，那么对这些状态变量的更新就可通过创建一个新对象并将该对象引用赋值给相应的引用型变量来实现。这个过程中，volatile 保障了原子性和可见性，从而避免了锁的使用。

场景四：使用 volatile 首先简易版读写锁。在该场景下，读写锁是通过混合使用锁和volatile变量而实现的，其中锁用于保障共享变量写操作的原子性，volatile 用于保证共享变量的可见性。

基于 volatile 的简易读写锁。
```java
public class Counter {
    private volatile long count;
    public long value() {
        return count;
    }
    public void increment() {
        synchronized (this) {
            count++;
        }
    }
}
```
通过一个分布式系统的负载均衡模块的设计与实现进一步讲解volatile的应用场景。

分布式系统的业务：通过网络连接调用下游部件提供的服务，即发送请求给下游部件。下游部件是一个集群环境（多主机对外提供相同的服务），因此，该系统调用其下游部件服务的时候需要进行负载均衡控制，即保障下游部件的各台主机上接收到的请求数分布均匀。

该系统在调用其下游部件时的负载均衡控制需要在不重启应用程序、服务器的情况下满足以下几点要求：
1. 需要支持多种负载均衡算法，如随机轮询算法和加权随机轮询算法；
2. 需要支持在系统运行过程中动态调整负载均衡算法，如从使用随机轮询算法调整为使用加权随机轮询算法；
3. 在调用下游部件过程中，下游部件不在线(如出现故障的主机)时需要被排除在外，即发送给下游部件的请求不能被派发给非在线主机；
4. 下游部件的节点信息科动态调整，如处于维护的需要临时删除一个节点后又将其重新添加回来；

这个负载均衡模块会涉及较多的 volatile 的使用。该系统负责调用其下游部件服务的类为 ServiceInvoker，如下面的清单：
```java
public class ServiceInvoker {
    // 保存当前类的唯一实例
    private static final ServiceInvoker INSTANCE = new ServiceInvoker();
    // 负责均衡实例，使用 volatile 变量保障可见性
    private volatile LoadBalancer loadBalancer;

    // 私有构造器
    private ServiceInvoker() {}

    // 获取当前类的唯一实例
    public static ServiceInvoker getInstance() { return INSTANCE; }

    /**
     * 根据制定的负责均衡器派发请求到特定的下游部件
     * @param request 派发的请求
     */
    public void dispatchRequest(Request request) {
        Endpoint endpoint = getLoadBalancer().nextEndPoint();
        if (null == endpoint) {
            // 省略其他代码
            return;
        }
        dispatchToDownstream(request, endpoint);
    }

    // 真正将指定的请求派发给下游部件
    private void dispatchToDownStream(Request request, Endpoint endpoint) {
        Debug.info("Dispatch request to " + endpoint + " : " + request);
        // 省略其他代码
    }

    public LoadBalancer getLoadBalancer() {
        // 读取负载平衡器
        return loadBalancer;
    }

    public void setLoadBalancer(LoadBalancer loadBalancer) {
        this.loadBalancer = loadBalancer;
    }
}
```

首先，我们使用 LoadBalancer 接口对负载均衡算法进行抽象，并为系统支持的每个负载均衡算法创建一个 LoadRebalancer 实现类，从而满足要求1。

```java
interface LoadBalancer {
    void updateCandidate(final Candidate candidate);
    Endpoint nextEndpoint();
}
```
接着，我们为 ServiceInvoker 设置一个实例变量 loadBalancer 来保存 LoadBalancer 实例。这里，我们使用 volatile 关键字修饰 loadBalancer，以此，如果一个线程修改了 loadBalancer 变量，能够对其他线程可见。这实现了对负载均衡算法的动态调整。

再来看加权轮询负载均衡算法的实现类 WeightedRoundRobinLoadBalancer:
```java
// 加权轮询负载均衡算法实现类
public class WeightedRoundRobinLoadBalancer extends AbstractLoadBalancer {
    // 私有构造器
    private WeightedRoundRobinLoadBalancer(Candidate candidate) {
        super(candidate);
    }

    // 通过改静态方法参见该类的实例
    public static LoadBalancer newInstance(Candidate candidate) {
        WeightedRoundRobinLoadBalancer loadBalancer = 
            new WeightedRoundRobinLoadBalancer(candidate);
        loadBalancer.init();
        return loadBalancer;
    }

    // 在该方法中实现负载均衡
    @Override
    Endpoint nextEndpoint() {
        Endpoint selectedEndpoint = null;
        int subWeight = 0;
        int dynamicTotalWeight;
        final double rawRnd = super.randoom.nextDouble();
        int rand;

        // 读取 volatile 变量 candidate
        final Candidate candidate = super.candidate;
        dynamicTotalWeight = candidate.totalWeight;
        for (Endpoint endpoint : candidate) {
            // 选取节点及计算总权重时跳过非在线节点
            if(!endpoint.isOnline) {
                dynamicTotalWeight -= endpoint.weight;
                continue;
            }
            rand = (int)(rawRnd * dynamicTotalWeight);
            subWeight += endpoint.weight;
            if (rand <= subWeight) {
                selectedEndpoint = endpoint;
                break;
            }
        }
        return selectedEndpoint;
    }
}
```
WeightedRoundRobinLoadBalancer 在选取下游部件节点(Endpoint)的时候会先判断相应节点是否在线，它会跳过非在线节点。再来看看 Endpoint 类的源码：
```java
// 表示下游部件节点
public class Endpoint {
    public final String host;
    public final int port;
    public final int weight;
    private volatile boolean online = true;
    public Endpoint(String host, int port, int weight) {
        this.host = host;
        this.port = port;
        this.weight = weight;
    }
    public boolean isOnline() {
        return online;
    }
    public void setOnline(boolean online) {
        this.online = online;
    }
}
```
这里 EndPoint 的 online 实例变量是个 volatile 变量，它用来表示相应节点的服务状态：是否在线。所有负载均衡算法实现类的抽象父类 AbstractLoadBalancer 内部都会维护一个心跳线程(Heartbeat Thread) 来定时检测下游部件各个节点的状态，并根据检测结果来更新相应 Endpoint 的 online 实例变量。如下列代码，这里心跳线程根据检测结果更新 volatile 变量 online 的值，而具体负载均衡算法实现类(如 WeightedRoundRobinLoadBalancer)则根据变量 online 的值决定其动作(跳过还是不跳过相应节点)：
```java
// 负载均衡算法抽象实现类，私有负载均衡算法实现类的父类
public abstract class AbstractLoadBalancer implements LoadBalancer {
    private final static Logger LOGGER = Logger.getAnonymousLogger();
    // 使用 volatile 变量替代锁(有条件替代)
    protected volatile Candidate candidate;
    protected final Random random;
    // 心跳线程
    private Thread heartbeatThread;

    public AbstractLoadBalancer(Candidate candidate) {
        if (null == candidate || 0 == candidate.getEndpointCount()) {
            throw new IllegalArgumentException("Invalid candidate " + candidate);
        }
        this.candidate = candidate;
        random = new Random();
    }

    public synchronized void init() throws Exception {
        if (null == heartbeatThread) {
            heartbeatThread = new Thread(new HeartTask(), "LB_Heartbeat");
            heartbeatThread.setDaemon(true);
            heartbeatThread.start();
        }
    }

    @Override
    public void updateCandidate(final Candidate candidate) {
        if (null == candidate || 0 == candidate.getEndpointCount()) {
            throw new IllegalArgumentException("Invalid candidate " + candidate);
        }
        // 更新 volatile 变量 candidate
        this.candidate = candidate;
    }

    // 留给子类实现的抽象方法
    @Override
    public abstract Endpoint nextEndpoint();

    protected void monitorEndpoint() {
        // 读取 volatile 变量
        final Candidate currCandidate = candidate;
        boolean isTheEndpointOnline;
        // 检测下游部件状态是否正常
        for (Endpoint endpoint: currCandidate) {
            isTheEndpointOnline = endpoint.isOnline();
            if (doDetect(endpoint) != isTheEndpointOnline) {
                endpoint.setOnline(!isTheEndpointOnline);
                // 省略日志记录代码
            }
        }
    }

    // 检测指定的节点是否在线
    private boolean doDetect(Endpoint endpoint) {
        // ...
    }

    private class HeartbeatTask implement Runnable {
        @Override
        public void run() {
            try {
                while (true) {
                    monitorEndpoints();
                    Thread.sleep(2000);
                }
            } catch (InterruptedException e) {
                // 什么都不做
            }
        }
    }
}
```
Candidate 的定义为：
```java
public final class Candidate implements Iterable<Endpoint> {
    // 下游部件节点列表
    private final Set<Endpoint> endpoints;
    // 下游部件节点的总权重
    public final int totalWeight;

    public Candidate(Set<Endpoint> endpoints) {
        int sum = 0;
        for (Endpoint endpoint: endpoints) {
            sum += endpoint.weight;
        }
        totalWeight = sum;
        this.endpoints = endpoints;
    }
}
```
Candidate 类保护了下游部件的节点列表以及列表中所有节点的总权重。这里的实例变量 totalWeight 作为一个冗余信息，作用是避免负载均衡算法每次都要计算总权重。如果我们要变更下游部件的节点信息，如删除一个节点，那么配置管理器需要调用 AbstractBalancer 子类的 updateCandidate 方法即可。updateCandidate 方法会直接更新 candidate 变量的值，这里 volatile 保障了这个操作的原子性与可见性。

## 实践：正确实现看似简单的单例模式

实现一个能够在多线程环境下正常运行且能够兼顾到性能的有实用价值的单例类却不是一件容易的事。

出于性能考虑，不少单例模式的实现会采用延迟加载(Lazy Loading)的方式。
```java
public class SingleThreadedSingleton {
    // 保存该类唯一的实例
    private static SingleThreadedSingleton instance = null;
    // 私有构造函数
    private SingleThreadedSingleton() {}
    // 创建并返回该类唯一的实例
    public SingleThreadedSingleton getInstance() {
        if (null == instance) {  // 操作1
            instance = new SingleThreadedSingleton(); // 操作2
        }
        return instance;
    }
}
```

在多线程环境下，getInstance() 中的 if 语句形成了 check-then-act 操作，他不是一个原子操作。由于代码中未使用任何同步机制，以此该程序的运行可能出现交错的情形：在 instance 还是 null 时，线程 T1 和 T2 同时执行操作1。接着 T1 执行操作2前，T2 已经执行了操作2，下一时刻，当 T1 执行到操作2的时候，尽管 instance 实际上已经不是 null，但 T1 仍然会再创建一个实例，这就导致了多个实例的创建。通过加锁可以解决这种问题。

```java
public class SimpleMultithreadSingleton {
    private static SimpleMultithreadSingleton instance = null;
    private SingleMultithreadSingleton() {}

    public static SimpleMultithreadSingleton getInstance() {
        synchronized (SimpleMultithreadSingleton.class) {
            if (null == instance) {
                instance = new SimpleMultithreadSingleton();
            }
            return instance;
        }
    }
}
```
这种方法实现的单例模式固然是线程安全的，但是这意味着 getInstance() 的任何一个执行线程都要申请锁，为了避免锁的开销，人们想到了一个 “聪明” 的方法：在进入临界区前先检查 instance 是否为 null，若不为 null，则 getInstance() 直接返回，否则才执行临界区。由于这种方法实现的 getInstance() 会两次检查 instance 的值是否为 null，以此它被称为双重检查锁定（Double-checked Locking，DCL）：
```java
public class IncorrectDCLSingleton {
    static private IncorrectDCLSingleton instance = null;
    private IncorrectDCLSingleton() {}
    static IncorrectDCLSingleton getInstance() {
        if (null == instance) {  // 操作1
            synchronized (IncorrectDCLSingleton.class) {
                if (null == instance) {  // 操作2
                    instance = new IncorrectDCLSingleton();  // 操作3
                }
            }
        }
        return instance;
    }
}
```
上述代码在可见性上分析没问题，但还需要考虑重排序。我们知道操作3可以分为以下伪代码所示的几个独立子操作：
```java
objRef = allocate(IncorrectDCLSingleton.class); // 1、分配存储空间
invokeConstructor(objRef); // 2、对象初始化
instance = objRef; // 3、将对象引用写入共享变量
```
JIT 编译器可能将上述子操作重排序为：1 -> 3 -> 2 。那会导致其他线程拿到一个没有被初始化的实例，导致程序出错。

解决方法也不难想到，只需要将 instance 变量采用 volatile 修饰即可。这实际上是利用了 volatile 关键字的两个作用：
- 保障可见性；
- 保障有序性：由于 volatile 能够禁止 volatile 变量写操作在与该操作之前的任何读、写操作进行重排序，以此 volatile 修饰的 instance 相当于禁止了JIT编译器以及处理器将子操作2重排序到子操作3，这保障了一个线程读取到 instance 变量所引用的实例时，该实例已经初始化完毕了。
```java
public class DCLSingleton {
    private static volatile DCLSingleton instance = null;
    private DCLSingleton() {}
    public static DCLSingleton getInstance() {
        if (null == instance) {
            synchronized(DCLSingleton.class) {
                if (null == instance) {
                    instance = new DCLSingleton();
                }
            }
        }
        return instance;
    }

}
```
考虑到双重检测锁定法实现上容易出错，我们可以采用另一种同样可以实现延迟加载效果且比较简单的方法：
```java
public class StaticHolderSingleton {
    private StaticHolderSingleton() {}
    private static class InstanceHolder {
        // 保存外部类的唯一实例
        final static StaticHolderSingleton INSTANCE = new StaticHolderSingleton();
    }
    public static StaticHolderSingleton getInstance() {
        return InstanceHolder.INSTANCE;
    }
}
```
**我们知道类的静态变量被初次访问会触发Java虚拟机对该类进行初始化。**因此，静态方法 getInstance() 被调用的时候，Java 虚拟机会初始化这个方法所访问的内部静态类 InstanceHolder，这使得 INSTANCE 被初始化，从而使 StaticHolderSingleton 类的唯一实例得以创建。由于类的静态变量只会被创建一次，因此 StaticHolderSingleton 只会被创建一次。

正确实现延时加载的单例模式还有一种更加简单的方法，那就是使用枚举类型(Enum)。
```java
public class EnumBasedSingletonExample {
    public static enum Singleton {
        INSTANCE;
        // 私有构造器
        Singleton() {
            // 初始化操作，赋值等
        }
    }
    public static void main(String[] args) {
        Thread t = new Thread() {
            @Override
            public void run() {
                Singleton.INSTANCE; // 获取单例进行操作
            }
        }
    }
}
```

## CAS 与原子变量

CAS (Compare and Swap) 是对一种处理器指令的称呼。不少多线程相关的 Java 标准库类的实现最终都会借助 CAS。

### CAS

前面我们提到一个简易写锁的 increment 方法，使用了一个内部锁来保障计数器自增这个操作的原子性：
```java
public void increment() {
    synchronized (this) {
        count++;
    }
}
```
实际上，这里使用锁来保障原子性有点杀鸡用牛刀的味道。锁固然是功能最强大、适用范围也很广泛的同步机制，但它的开销也是最大的。另外，volatile 虽然开销小一点，但是它无法保障 count++ 这种自增操作的原子性。事实上，保障像自增这种比较简单的操作的原子性，我们有更好的选择 -- CAS。CAS 能够将 read-modify-write 和 check-and-act 之类的操作转换为原子操作。

CAS 的伪代码如下：
```java
boolean compareAndSwap(Variable V, Object A, Object B) {
    if (A == V.get()) {
        V.set(B);
        return true;
    }
    return false;
}
```
CAS 是一个原子的 if-then-act 的操作，其背后的假设是：当一个客户(线程)执行CAS操作的时候，如果变量 V 的当前值和客户全球CAS提供的A(即变量的旧值)是相等的，那么就说明其他线程没有修改过变量 V 的值（这种假设不一定总是直接成立）。执行 CAS 时如果没有其他线程修改过变量V的值，那么下手最快的客户就会抢先将变量V的值更新为B，而其他客户的更新请求会失败。这些失败的客户线程通常可以选择再次尝试，直到成功为止。

使用 CAS 实现线程安全的计数器：
```java
public class CASBasedCounter {
    private volatile long count;

    public long value() {
        return count;
    }

    public void increment() {
        long oldValue;
        long newValue;
        do {
            oldValue = count; // 读取共享变量的当前值
            newValue = oldValue+1; // 计算共享变量的新值
        } while(/*使用CAS来更新共享变量的值*/ compareAndSwap(oldValue, newValue));
    }

    // 该方法是一个实例方法，且共享变量 count 是当前类的实例变量，因此这里没有必要在方法参数中声明一个表示共享变量的参数
    private boolean compareAndSwap(long oldValue, long newValue) {
        ... cas 的具体实现 ...
    }
}
```
上述 increment 方法中的 do-while 循环用于更新共享变量失败的时候继续重试，直到更新成功。这也是许多基于 CAS 的算法的代码模板(伪代码)：
```java
do {
    oldValue = V.get();
    newValue = calculate(oldValue);
} while(!compareAndSwap(oldValue, newValue));
```
即在循环体中读取共享变量 V 的旧值(当前值)A，并以该值为输入，经过一系列操作计算共享变量的新值 B，接着调用 CAS 试图将 V 的值更新为 B。若更新失败(说明更新期间其他线程修改了共享变量V的值)，则继续重试，直到成功。

需要注意的是，CAS 只是保障了共享变量更新这个操作的原子性，它并不保障可见性，因此在上述代码中，我们仍然采用 volatile 修饰共享变量 count。

### 原子操作工具：原子变量类

原子变量类(Atomics)是基于 CAS 实现的能够保障对共享变量进行 read-modify-write 更新操作的原子性和可见性的一组工具类。这里所谓的 read-modify-write 更新操作，是指对共享变量的更新不是一个简单的赋值操作，而是变量的新值依赖于变量的旧值，例如自增操作 “count++”。由于 volatile 无法保障自增操作的原子性，而原子变量类的内部实现通常借助一个 volatile 变量并保障对该变量的 read-modify-write 更新操作的原子性，因此它可以被看作是增强型的 volatile 变量。原子变量类一共有12个，可以被分为4组：

- 基础数据型：AtomicInteger / AtomicLong /AtomicBoolean
- 数组型：AtomicIntegerArray / AtomicLongArray / AtomicReferenceArray
- 字段更新型：AtomicIntegerFieldUpdate / AtomicLongFieldUpdate / AtomicReferenceFieldUpdate
- 引用型：AtomicReference / AtomicStampedReference / AtomicMarkableReference

AtomicLong 类继承自 Number 类。其内部维护了一个 long 型 volatile 变量。AtomicLong 类对外暴露了相关方法用于实现针对该 volatile 变量的自增(自减)操作，这些操作是基于CAS实现的原子性操作。AtomicLong 类的常用方法为：

- `public final long get()`: 获取当前实例的当前数值，相当于读取一个 volatile 变量。
- `public final long getAndIncrement()`
- `public final long getAndDecrement()`
- `public final long incrementAndGet()`
- `public final long decrementAndGet()`
- `public final void set(long newValue)`

AutomicBoolean 类咋一看似乎有些多余，因为对布尔型变量的写操作不来就是原子操作。实际上，这里需要注意更新操作并不一定是简单的赋值。AtomicBoolean 类如同其他原子操作一样，它们要实现以 read-modify-write 操作的原子性。

我们知道，即使采用 volatile 关键字修饰数组变量，也无法保障对应元素的读、写操作的可见性与原子性。为此，java 引入了 AtomicIntegerArray / AtomicLongArray 和 AtomicReferenceArray 这 3 个类。这几个类与 AtomicLong 类似，只不过我们在调用这些类的相关原子操作方法时，需要多指定一个数组下标。

前面我们讲到，CAS 实现原子操作背后的一个假设：共享变量的当前值与当前线程所提供的旧值相同，我们就认为这个变量没有被其他线程修改过。实际上，这个假设不一定总是成立，或者说它成立是有条件的。例如，对于共享变量 V，当前线程看到它的值为 A 的那一刻，其他线程已经将它更新为B，接着，在当前线程执行 CAS 时，该变量的值又被其他线程更新为 A，那么此时我们是否认为变量 V 没有被其他线程更新过呢？或者说这种结果是否可接受？这就是 **ABA问题** 。

ABA 问题是否可以被接受与实现的算法有关，某些情况下我们无法容忍ABA问题。规避 ABA 问题也不难，那就是为共享变量的更新引入一个修订号(也称时间戳)。每次更新共享变量时，相应地修订号的值会增加1。AtomicStampedReference 类就是基于这种思想而产生的。

字段更新器(AtomicIntegerFieldUpdate、AtomicLongFieldUpdate、AtomicReferenceFieldUpdate)这3个类相对来说更加底层，可以将其理解为对 CAS 的一种封装，而原子变量类中的其他类都可以利用这几个类来实现。

## 对象的发布与逸出

线程安全问题产生的前提是多个线程共享变量。即使是 private 变量也可能被多个线程共享，例如：
```java
public class Example {
    private Map<String, Integer> registry = new HashMap<String, Integer>();
    public void someService(String in) {
        // 访问 registry
    }
}
```
多个线程共享变量还有其他途径，它们被统称为对象发布(publish)。对象发布是指使对象能够被其作用域之外的线程访问。常见的对象发布形式除了上述的共享 private 变量外，还包括以下几种。
1. 发布形式1：将对象引用存储到 public 对象中。从面向对象编程角度来看，这种发布形式不太提倡，因为它违反了信息封装(Information Hiding)的原则，不利于问题定位；
2. 在非 private 方法（包括 public、protected、package 方法）中返回一个对象；
3. 创建内部类，使得当前对象(this)能够被这个内部类使用；
4. 通过方法调用将对象传递给外部方法；

### 对象的初始化安全：重访 final 与 static

Java 中类的初始化实际上也采取了延迟加载的技术，即一个类被 Java 虚拟机加载后，该类的所有静态变量的值都仍然是其默认值(引用型变量的默认值为 null，boolean 变量的默认值为 false)，直到有个线程初次访问了该类的任意一个变量，才使这个类被初始化——类的静态初始化块被执行，类的所有静态变量被赋予初始值。静态方法和静态变量初始化的顺序与代码顺序一致。

static 关键字在多线程环境下尤其特殊含义，它能够保证一个线程即使在未使用其他同步机制的情况下也总是能够读取到一个类的静态变量的初始值(而不是默认值)。但是，这种可见性保障仅限于线程初次读取该变量。如果静态变量在相应类初始化完毕后被其他线程更新过，那么一个线程要读取该变量的相对新值仍然需要借助锁、volatile关键字等同步机制。

对于引用型静态变量，static 能保障一个线程读取到该变量的初始化值时，该值所指向(引用)的对象已经初始化完毕。

由于重排序作用，一个线程读取到一个对象引用时，该对象可能尚未初始化完毕，即这些线程可能读取到该对象字段的默认值而不是初始值。在多线程环境下，final 关键字有其特殊的作用：当一个对象被发布到其他线程时，该对象的所有 final 字段都是初始化完毕的。非 final 字段没有这种保障。对于引用型final字段，final 关键字进一步确保该字段所引用的对象已经初始化完毕，即这些线程读取该字段所引用的对象的各个字段时，所读取到的值都是相应的初始值。

### 安全发布与逸出

安全发布就是指对象以一种线程安全的方式被发布。当一个对象的发布出现我们不期望的结果或者对象发布本身不是我们期望的对象时，我们就称该对象逸出(Escape)。逸出应该是我们要尽量避免的，因为它不是一种安全发布。

创建内部类是最容易导致对象逸出的一种发布，它具体包括一下几种形式：
- 在构造器中将 this 复制给一个共享变量；
- 在构造器中将 this 作为方法参数传递给其他方法；
- 在构造器中启动基于匿名类的线程；

在启动工作者线程时实现对象安全发布范例：
```java
public class SafeObjPublishWhenStartingThread {
    private final Map<String, String> ObjectState;

    private SaftObjPublicWhenStartingThread(Map<String, String> objectState) {
        this.obectState = objState;
        // 不在构造器中启动工作者线程，以避免 this 逸出
    }
    private void init() {
        // 创建并启动工作者线程
        new Thread() {
            @Override
            public void run() {
                // 访问外层类实例的状态变量
                String value = objectState.get("someKey");
                Debug.info(value);
                // 省略其他代码
            }
        }.start();
    }
    // 工厂方法
    public static SafeObjPublishWhenStartingThread newInstance(Map<String, String> objState) {
        SafeObjPublishWhenStartingThread instance = new SafeObjPublishWhenStartingThread(objState);
        instance.init();
        return instance;
    }
}
```


