# 多线程基础

## 线程的优雅关闭

### stop() 与 destory() 函数

线程是“一段运行中的代码”，或者说是一个运行中的函数。既然是一段运行中，就存在一个最基本的问题：运行到一半的线程能否强制杀死？

答案肯定是不能。在 Java 中有 stop() / destory() 之类的函数，但这些函数都是官方明确不建议使用的，原因很简单，如果强制杀死线程，则线程中所使用的资源，例如文件描述符、网络连接等不能正常关闭。

因此，一个线程一旦运行起来，就不要去强行打断它，合理的关闭办法是让其运行完(也就是函数执行完毕)，干净地释放掉所有资源，然后退出。如果是一个不断循环运行的线程，就需要线程间通信，让主线程通知其退出。

在下面的一段代码中：在 main 函数中开了一个线程，不断循环打印。请问 main 函数退出以后，该线程是否会被强制退出？整个进程是否会被强制退出？
```java
    public static void main(String[] args) {
        System.out.println("main enter!");
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    try {
                        System.out.println("t1 is executing");
                        Thread.sleep(500);
                    } catch (InterruptedException e) {

                    }
                }
            }
        });
        t1.start();
        System.out.println("main exit");
    }
```
答案是不会。在 C 语言中，main 函数退出后，整个程序都退出，但在 Java 中并非如此。

对于上面的程序，在`t1.start()`前面加一行代码 `t1.setDaemon(true)`。当 main(..) 函数退出后，线程 t1 就会退出，整个进程也会退出。

当在一个 JVM 进程里面开多个线程时，这些线程被分为两类：守护线程和非守护线程。默认开的都是非守护线程。在 Java 中有一个规定：当所有的非守护线程退出后，整个 JVM 进程才会退出。例如，垃圾回收线程就是守护线程，它们在后台默默工作，当开发者的所有前台线程(非守护线程)都退出之后，整个JVM进程就退出了。

### 设置关闭的标志位

在上面的代码中，线程是一个死循环。但在实际工作中，开发人员通常不会这么写，而是通过一个标志位来实现。
```java
static class MyThread extends Thread {
        private volatile boolean stopped = false;
        public void run() {
            while(!stopped) {
                ... ...
            }
        }
        public void setStop() {
            this.stopped = true;
        }
    }

    @SneakyThrows
    public static void main(String[] args) {
        MyThread t = new MyThread();
        t.start();
        t.setStop();
        t.join();
    }
```
但上面的代码有一个问题：如果 MyThread 在循环中阻塞在某个地方，例如里面调用了 object.wait() 函数，那么它可能永远没有机会再执行 `while(!stopped)` 代码，也就一直无法退出。

此时就要用到下面所讲的 InterruptedException 与 interrupt 函数。

## InterruptedException 异常与 interrupt 函数

Interrupt 这个词很容易让人产生误解。从字面意思来看，好像是说一个线程运行到一半，把它中断了，然后抛出了 InterruptedException 异常，其实并不是。仍以上面的代码为例，假设 while 循环中没有调用任何的阻塞函数，就是通常的算术运算，或者打印一行日志，如下所示：
```java
public void run() {
    while (!stopped) {
        int a = 1, b = 2;
        int c = a + b;
        System.out.println("threads executing");
    }
}
```
这个时候，在子线程中调用一句：`t.interrupt()`，请问线程会不会抛出异常？答案是不会。

再举一个例子，假设这个线程阻塞在 synchronized 关键字的地方，正准备拿锁，如下代码所示。
```java
public void run() {
    while(!stopped) {
        synchronized(obj1); // 线程阻塞在这个地方
        int a = 1, b = 2;
        int c = a + b;
        System.out.println("thread is executing");
    }
}
```
这个时候，在主线程中调用一句 `t.interrupt()`，该线程也不会抛出异常。

实际上，**只有那些声明了会抛出 InterruptedException 的函数才会抛出异常，也就是下面这些采用的函数：**
```java
public static native void sleep(long millis) throws InterruptedException {}
public final void wait() throws Interruption {...}
public final void join() throws Interruption {...}
```

### 轻量级阻塞与重量级阻塞

能够被中断的阻塞称为轻量级阻塞，对应的线程状态是 WAITING 或者 TIMED_WAITING。

下面是调用不同的函数后，一个线程完整的状态迁移过程。

1、NEW：初始状态；2、WAITING: 等待；3、TIMED_WAITING；4、RUNNING；5、READY；6、TERMINATED

NEW -> RUNNING/READY: t.start()
RUNNING/READY -> WAITING: Object.wait() / Thread.join() / LockSupport.park()
WAITING -> RUNNING/READY: Object.notify() / Object.notifyAll() / LockSupport.unpark(Thread)
RUNNING/READY -> BLOCK: Object.synchronized
BLOCK -> RUNNING/READY: 获取到锁
RUNNING/READY -> TIMED_WAITING: Object.wait(long) / Thread.sleep(long) / Thread.join(long) / LockSupport.park(long)
TIMED_WAITING -> RUNNING/READY: Object.notify() / Object.notifyAll() / LockSupprot.unpark(Thread)
RUNNING/READY -> TERMINATED： 执行完成；

初始线程处于 NEW 状态，调用 start() 后开始执行，进入 RUNNING 或 READY 状态。
如果没有调用任何阻塞函数，线程只会在 RUNNING 和 READY 之间切换，也就是系统的时间片调度。这两种状态的切换是操作系统完成的，开发者基本上没有机会接入，除了可以调用 yield() 函数，放弃对 CPU 的占用。

一旦调用了图中的任何阻塞函数，线程就会进入 WAITING 或者 TIMED_WAITING 状态，两者的区别只是前者为无限期阻塞，后者则传入了一个时间参数，阻塞一个有限的时间。如果使用了 synchronized 关键字或者 synchronize 块，则会进入 BLOCKED 状态。

除了常用的阻塞/唤醒函数，还有一对不常用的阻塞/唤醒函数，LockSupport.park() / unpark()。这对函数非常关键，Concurrent 包中 Lock 的实现依赖于这一对操作原语。

t.interrupt() 的精确含义是唤醒轻量级阻塞(不包括重量级阻塞或非阻塞)，而不是字面的“终端一个线程”。

### t.isInterrupted() 与 Thread.interrupted() 区别

因为 t.interrupt() 相当于给线程发送了一个唤醒的信号，所以如果线程此时恰好处于 WAITING 或者 TIMED_WAITING 状态，就会抛出一个 InterruptedException，并且线程被唤醒。如果线程没有被阻塞，则线程什么都不做。但在后续，线程可以判断自己是否收到过其他线程发来的中断信号，然后做一些对应的处理。

t.isInterrupted() 和 Thread.interrupted() 就是用来判断自己是否收到过终端信号的，前者非静态函数，后者是静态函数。二者的区别在于：t.isInterrupted() 只读取中断状态，不修改状态；Thread.interrupted() 不仅读取中断状态，还会重置中断标志位，如果重置了中断位，那么调用 wait 就不会抛异常，在进入 wait 的异常处理函数后，中断为也会被清理。

## sychronized 关键字

对不熟悉多线程原理的人来说，很容易误解 synchronized 关键字：它通常加在所有的静态成员函数和非静态函数的前面，表面上看起来好像是函数之间互斥的，其实不是。synchronized 关键字其实是给某个对象加锁，例如：
```java
class A {
    public void synchronized f1() { ... }
    public static void synchronized f2() { ... }
}
```
等价于：
```java
class A {
    public void f1() {
        synchronized (this) { ... }
    }
    public static void f2() {
        synchronized (A.class) { ... }
    }
}
```
对于非静态成员函数，锁其实是加在对象实例上的；对于静态成员函数，锁是加在 A.class 上面的。

### 锁的本质

无论使用声明编程语言，只要是多线程的，就一定会涉及锁。

多个线程要访问同一个资源。线程就是一段段运行的代码，资源就是一个变量、一个对象或者一个文件等，而锁就是要实现线程对资源的访问控制，保证同一时间只有一个线程去访问某一个资源。打个比方，线程就是一个个有课，资源就是一个待参观的房子，这个房子同一时间只允许一个游客进去参观，当一个人出来之后下一个人才能进去。而锁就是这个房子门口的守卫。**如果同一个时间允许多个游客参观**，锁就变成了信号量。

从程序角度来看，锁其实就是一个“对象”，这个对象要完成以下几件事情：
1. 这个对象内部得有一个标志位：state 变量，记录自己有没有被某个线程占用。最简单的情况是这个 state 有0、1两个取值，0表示没有线程占用该锁，1表示被占用。
2. 如果这个对象被某个线程占用，它得记录这个线程的 thread id，直到自己是被哪个线程占用了。
3. 这个对象还要维护一个 thread id list，记录其他所有阻塞的、等待拿这个锁的线程，在当线程释放锁之后，从这个 thread id list 中取出一个线程唤醒。

既然锁是一个对象，要访问的共享资源也是一个对象，两个对象可以合成一个对象。代码就变成了 `synchroinzed(this) { ... }`，我们要访问的共享资源是对象 a，锁也是加在对象 a 上面的。当然，也可以另外新建一个对象，代码变成了 `synchronized(obj) { ... }`，这个时候，访问的共享资源是对象 a，而锁是加在新建的对象 obj1 上面的。

资源和锁合二为一，使得在 Java 中，synchronized 关键字可以加在任何对象的成员上面。这因为这个对象既是共享资源，同时也具备了锁的功能。

### synchronized 实现原理

答案在 java 的对象中。在对象中，有一块数据叫做 Mark Word。在 64  位机器上，Mark Word 是8个字节(64bit)，这64位有2个重要字段：锁标志位和占用该锁的 thread ID。因为不同版本的数据结构存在差异，不做进一步讨论。

## wait() 与 notify()

### 生产组-消费者模型

生产组-消费者模型是一个常用的多线程编程模型。一个内存队列，多个生产者线程往内存队列中放数据，多个消费者线程从队列中取数据。要实现这样一个编程模型，需要做下面几件事：
1. 内存队列本身要加锁，才能实现线程安全。
2. 阻塞：当内存队列慢时，生产者放不进去时，会被阻塞；当内存队列是空时，消费者无事可做，会被阻塞。
3. 双向通知：消费者被阻塞之后，生产者放入新数据，要 notify() 消费者；反之，生产者被阻塞之后，消费者消费了数据要 notify() 生产者。

第一件事情必须做。2、3不一定要做，例如可以采用一个简单的办法：生产者放不进去之后，睡眠几百毫秒之后再重试，消费者类似。但这个办法效率低下，也不实用。所以我们只讨论如何阻塞，如何通知的问题。

1. 如何阻塞？
办法1：线程阻塞自己，也就是生产者、消费者线程各自调用 wait() 和 notify()。
办法2：用一个阻塞队列，当取不到或放不进去数据的时候，入队/出队函数本身就是阻塞的。这也是 BlockingQueue 的实现。

2. 如何双向通知？
办法1：wait() 与 notify() 机制；
办法2：condition 机制。

### 为什么必须和 synchronized 一起使用

在 java 中，wait() 和 notify() 是 Object 的成员函数，是基础中的基础。为什么 Java 要把 wait() 和 notify() 放在如此基础的类中，而不是作为像 Thread 一类的成员函数，或者其他类的成员函数呢？

在回答这个问题之前，必须先回答为什么 wait() 和 notify() 必须和 synchronized 一起使用？请看下面的代码：
```java
class A {
    private Object obj1 = new Object();
    public void f1() {
        synchronized (obj1) {
            ...
            obj1.wait();
            ...
        }
    }
    public void f2() {
        synchronized (obj2) {
            ...
            obj2.notify();
            ...
        }
    }
}
```
或者下面的代码：
```java
class A {
    public synchronized f1() {
        ...
        this.wait();
        ...
    }
    public synchronized f2() {
        ...
        this.notify();
        ...
    }
}
```
然后开两个线程，线程A调用f1()，线程B调用f2()。答案很明显：两个线程之间要通信，对于同一个对象来说，一个线程调用该对象的 wait()，另一个线程调用该对象的 notify()，该对象本身就需要同步！所以在调用 wait() / notify() 之前，要限通过 synchronized 关键字同步给对象，也就是给该对象加锁。

### 为什么 wait() 时候必须释放锁

当线程 A 进入 synchronized(obj1) 中之后，也就是对 obj1 上了锁。此时调用 wait() 进入阻塞状态，一直不能退出 synchronized 代码块，那么线程 B 永远无法进入 synchronized 代码块；那么，线程B永远无法进入 synchronized(obj1) 同步块中，永远没有机会调用 notify()，就会导致死锁。

这里涉及一个关键问题：在 wait() 的内部，会先释放锁 Obj1，然后进入阻塞状态，之后被另一个线程用 notify() 唤醒，去重新拿锁！其次，wait() 调用完成后，执行后面的业务逻辑代码，然后退出 synchronized 同步块，再次释放锁。

wait() 内部的伪代码如下：
```java
wait() {
    // 释放锁
    // 阻塞，等待被其他线程 notify
    // 重新拿锁
}
```
只有如此，才能避免上面所说的死锁问题。

### wait() 与 notify() 的问题

以上述的消费者-消费者模型来看，其伪代码大致如下：
```java
public void enqueue() {
    synchronized (queue) {
        while (queue.isFull()) queue.wait();
        ... // 数据入队列
        queue.notify(); // 通知消费组，数据中有数据了
    }
}
public void dequeue() {
    synchronized (queue) {
        while(queue.isEmpty()) queue.wait();
        ... // 出队列
        queue.notify(); // 通知生产组，队列中有空位了，可以继续放数据
    }
}
```
生产者本来指向通知消费者，但它把其他生产者也通知了；消费组本来只想通知生产者，但它把其他的消费组也通知了。原因是 wait() 和 notify() 作用的对象和 synchronize() 所占用的对象是同一个，只能有一个对象，无法区分队列空和队列满这两个条件。这正是 Condition 要解决的问题。

## volatile 关键字

volatile 这个关键字很不起眼，其使用场景不像 synchronized、wait()、notify() 那么明显。正因为其晦涩，volatile 关键字可能是在多线程编程领域被误解最多的一个。

### 64 位写入的原子性(Half Write)

举一个简单的例子，对于一个 long 类型变量的赋值和取值操作而言，在多线程场景下，线程 A 调用 `set(100)`，线程B调用`get()`，在某些场景下，返回值可能不是100。
```java
public class Example1 {
    private long a = 0;
    public void set(long a) {  // 线程 A 调用 set(100)
        this.a = a;
    }
    public long get() {  // 线程B调用get()，返回值不一定为100
        return a;
    }
}
```
这点有点反直觉，如此简单的一个赋值和取值操作，在多线程下为什么会不对呢？这是因为 JVM 的规范并没有要求 64 位的long或double写入是原子的。在32为机器上，一个64位变量的写入可能被拆分为两个32为的写操作来执行。这样一来，读的线程可能读到“一半的值”。解决办法也很简单，在 long 前面加上 volatile 关键字。


### 内存可见性

不仅 64 位，32位或者位数更小的赋值和取值操作，其实也有问题。之前1.1节，线程关闭的标志位 stopped 为例，它是一个 boolean 类型的数字，也可能出现主线程把它设置成 true，而工作线程读到的却是 false 的情形，这就更反直觉了。

注意，这里并不是说永远读到的都是 false，而是说一个线程写完之后，另一个线程立即去读，读到的是false，也就是 “最终一致性” 而不是 “强一致性”。如果实现了一把自旋锁，就会出现一个线程把状态置为 true，另一个线程读到的却是 false，然后两个线程都会拿到这把锁的问题。

所以，我们所说的“内存可见性”，指的是“写完之后立即对其他线程可见”，它的方面不是不可见，而是“稍后才能可见”。解决这个问题很容易，给变量加上 volatile 关键字即可。

### 重排序：DCL 问题

单例模式的线程安全的写法不止一种，常用写法为 DCL (Double Checking Locking)，如下所示：
```java
public class Singleton {
    private static Singleton instance;
    public static Singleton getInstance() {
        if (null == instance) {  // DCL
            synchronized (Singleton.class) {  // 为了性能，延时使用 synchronized
                if (null == instance) {
                    instance = new Singleton();  // 有问题的代码
                }
            }
        } else {
            return instance;
        }
    }
}
```
上述的 instance = new Singleton() 代码有问题，其底层会分为三个操作：
1. 分配一块内存；
2. 在内存上初始化成员变量；
3. 将 instance 应用指向内存；

在这三个操作中，2、3可能重排序，即先把 instance 指向内存，再初始化成员变量，因为两者没有先后的依赖关系。此时，另外一个线程可能拿到一个没有经过初始化的对象。这时，直接访问对象中的成员变量，就可能出错。这就是典型的“构造函数溢出”问题。解决办法也很简单，就是为 instance 变量加上 volatile 修饰。

通过上面的例子，可以总结出 volatile 的三重功能：
1. 64位数据写入的原子性；
2. 一个线程更新共享变量后，对其他线程的可见性；即内存可见性；
3. 禁止重排序；

## JMM 与 happen-before

### 为什么存在“内存可见性”问题

要解释这个问题，就涉及现代 CPU 的架构。

例如一个 CPU 4核下，每个核有 L1、L2 缓存，之后是各个核公用的 L3 缓存，最后是主内存。

因为存在 CPU 缓存一致性协议，例如 MESI，多个 CPU 之间的缓存不会出现不同步的问题，，不会有内存可见性问题。

但是，缓存一致性协议对性能有很大的损耗，为了解决这个问题，CPU 的设计者们在这个基础上又进行了各种优化。例如，在计算单元和L1之间加上了 Store Buffer、Load Buffer，还有其他各种 Buffer。

L1、L2、L3 和主内存之间是同步的，有缓存一致性协议保证的，但是 Store Buffer、Load Buffer 和 L1 之间确实异步的。也就是说，往内存中写入一个变量，这个变量会保存在 Store Buffer 里面，稍后才异步地写入 L1 中，同时同步写入主内存中。

在操作系统内核家督下的 CPU 缓存模型：多 CPU，每个 CPU 多核，每个核上面可能有多个硬件线程，对于操作系统而言，相当于一个个的逻辑 CPU，每个逻辑 CPU 都有自己的缓存，这些缓存和主内存之间不是完全同步的。

### 重排序与内存可见性的关系

Store Buffer 的延迟写入是重排序的一种，称为内存重排序(Memory Ordering)。除此之外，还有编译器和 CPU 的指令重排序。下面对重排序做一个分类：
1. 编译器重排序。对于没有先后依赖关系的语句，编译器可以重新调整语句的执行顺序。
2. CPU 指令重排序。在指令级别上，让没有依赖关系的多条指令并行。
3. CPU 内存重排序。CPU 有自己的缓存，指令的执行顺序和写入主内存的顺序不完全一致。

在三种重排序中，第三类就是造成“内存可见性”问题的主因，下面再举一个例子进一步说明问题：
```
线程1：
    X = 1
    a = Y
线程2：
    Y = 1
    b = X
```
假设 X、Y 是两个全局变量，初始时候 X = 0，Y = 0。请问，这两个线程执行完毕后，a、b 的正确结果应该是什么？

可能的结果：a = 0, b = 0; a = 0, b = 1; a = 1, b = 0; a = 1, b = 1;

这就是一个有意思的地方，虽然线程1觉得自己是按代码顺序正常执行的，但在线程2看来，a = Y 和 X = 1 顺序却是却是颠倒的。指令没有重排序，是写入内存的操作被延迟了，也就是内存被重排序了，这就造成了内存可见性问题。

### as-if-serial 语义

对开发者而言，当然希望不要有任何的重排序，这样理解起来最简单，指令执行顺序和代码顺序一致。但是，从编译器和 CPU 的角度来看，希望尽最大可能进行重排序可以重排序的，什么场景下不能重排序呢？
1. 单线程程序的重排序规则：无论什么语言，站在编译器和CPU的角度来看，不管怎么重排序，单线程程序的执行结果不能改变，这就是单线程程序的重排序规则。换句话说，只有之间没有数据依赖，如上例所示，编译器和 CPU 都可以任意重排序，因为执行结果不会改变，代码看起来就像完全串行地一行行地从头到尾执行，这也就是 as-if-serial 语义。对于多线程程序而言，编译器和 CPU 可能 做了重排序，但开发者感知不到，也不存在内存可见性问题。

2. 多线程程序的重排序规则

编译器和 CPU 的这一行为对于多线程程序没有影响，但对于多线程程序来说，线程之间的数据依赖性它复杂，编译器和CPU没有办法完全理解这类依赖性并据此作出最合理的优化。所以，编译器和CPU只能保证每个线程的 as-if-serial 语义。线程之间的数据依赖和相互影响，需要编译器和 CPU 的上层来确定。上层要告知编译器和 CPU 在多线程场景下什么时候可以重排序，什么时候不能重排序。

编译器和CPU遵守了 as-if-serial 语义，保证了每个线程内部都是“看似完全串行的”。但多个线程会相互读取和写入共享的变量，对于这种相互影响，编译器和CPU不会考虑。

### happen-before 是什么

为了明确定义在多线程场景下什么时候可以重排序，什么时候不能重排序，Java 引入了 JMM (Java Memory Model)，也就是 Java 内存模型。这个模型就是一套规范，对上是 JVM 和开发者之间的协定，对下，是 JVM 和编译器、CPU 之间的协定。

定义这套规范，其实是要在开发者写程序的方便性和系统运行的效率之间找到一个平衡点。一方面，要让编译器和 CPU 可以灵活地重排序；另一方面，要对开发者做一些承诺，明确告知开发者不需要感知什么样的重排序，需要感知什么样的重排序。然后，根据需要决定这些重排序对程序是否有影响，如果有影响，就需要开发者显式地通过 volatile、synchronized 等线程同步机制来禁止重排序。

为了表示这个规范，JMM 引入了 happen-before，使用 happen-before 描述两个操作之间的内存可见性。那么，happen-before 是什么呢？

如果 A happen-before B，意味着 A 的执行结果必须对 B 可见，也就是保证跨线程的内存可见性。A happen before B 不代表 A 一定在 B 之前执行。因为，对于多线程程序而言，两个操作的执行顺序是不确定的。happen-before 只确保如果 A 在 B 之前执行，则 A 的执行结果必须对 B 可见。定义了内存可见性的约束，也就定义了一系列重排序的约束。

基于 happen-before 的这种描述方法，JMM 对开发者作出了一系列承诺：
1. 单线程中的每个操作，happen-before 对应该线程中任意后续操作(as-if-serial语义保证)
2. 对 volatile 变量的写入，happen-before 对应后续对这个变量的读取
3. 对 synchronized 的解锁，happen-before 对应后续对这个锁的加锁
... ...

对非 volatile 变量的写入和读取，不在这个承诺范围之列。

通俗来说，就是 JMM 对编译器和 CPU 来说哦，volatile 变量不能重排序；非 volatile 变量可以任意重排序。

### happen-before 的传递性

若 A happen-before B，B happen-before C，则 A happen-before C。例如下面的例子：
```java
class A {
    private int a = 0;
    private volatile int c = 0;
    public void set() {
        a = 5;  // 操作 1
        c = 1;  // 操作 2
    }
    public void get() {
        int d = c; // 操作 3
        return a;  // 操作 4
    }
}
```

## 内存屏障

为了禁止编译器重排序和 CPU 重排序，在编译器和 CPU 层面都有对应的指令，也就是内存屏障(Memory Barrier)。这也正是 JMM 和 happen-before 规则的底层实现原理。

编译器的内存屏障，只是为了告诉编译器不要对指令进行重排序，当编译器完成之后，这种内存屏障就消失了，CPU 并不会感知编译器中内存屏障的存在。而 CPU 的内存屏障是 CPU 提供的指令，可以由开发者显式调用。下面主要将 CPU 的内存屏障。

## final 关键字

### 构造函数溢出问题

考虑下面的代码：
```java
public class Example {
    private int i;
    private int j;
    private static Example obj;
    public Example() {
        i = 1;
        j = 2;
    }
    public static void write() {  // 线程 A 先执行 write
        obj = new Example();
    }
    public static void read() {
        if(obj!=null) {
            int a = obj.i;
            int b = obj.j;  // a, b 是否一定等于 1, 2
        }
    }
}
```
和 DCL 的例子类似，也就是构造函数溢出问题。obj = new Example() 分解成 3 个操作：
1. 分配一块内存；
2. 在内存上初始化 i=1，j=2；
3. 将obj指向这块内存。

其中 2、3可能重排序。对于构造函数溢出，通俗来讲就是一个对象的构造不是原子的，当一个线程正在构造对象时，另一个线程却可以读到未构造好的“一半对象”。

### final 的 happen-before 语义

要解决这个问题，不止有一种办法：
1. 给 i、j 都加上 volatile 关键字；
2. 为 read / write 函数都加上 synchronized 关键字；

如果i，j只需要初始化一次，则后续值就不会再变了，还有办法3，为其加上 final 关键字。之所以能解决问题，是因为同 volatile 一样，final 关键字也有相应的 happen-before 语义。
1. 对 final 域的写(构造器内部)，happen-before 于后续对 final 与所在对象的读；
2. 对 final 与所在对象的读，happen-before 与后续对 final 域的读；

### happen-before 规则总结

1. 单线程中的每个操作，happen-before 于该线程中任意后续操作；
2. 对 volatile 变量的写，happen-before 于后续对这个变量的读；
3. 对 synchronized 的解锁，happen-before 与后续对这个锁的加锁；
4. 对 final 变量的写，happen-before 与 final 域对象的读，happen-before 于后续对 final 变量的读。

四个规则加上 happen-before 的传递性，就构成 JMM 对开发者的整个承诺。在这个承诺之外的部分，程序可能被重排序，都需要开发者小心地处理内存可见性问题。

## 综合应用

提到多线程编程，就绕不开“锁”。在 Java 中就是指 synchronized 和 Lock。在 Linux 中，主要是 pthread 和 mutex。但锁又是性能杀手，所有有很多前辈大师研究如何可以不用锁，也能实现线程安全。无锁编程是一个庞大而深入的话题，既涉及底层的 CPU 架构(如前面讲的内存屏障)，又涉及不同语言的具体实现。下面总结了常用的集中无锁编程的场景。

### 一写一读的无锁队列：内存屏障

一写一读的无锁队列，即 Linux 内核的 kfifo 队列，一写一读两个线程，不需要锁，只需要内存屏障。

### 一写多读的无锁队列：volatile 关键字

### 多写多读的无锁队列：CAS

同内存屏障一样，CAS(Compare and Set)也是CPU提供的一种原子指令。基于 CAS 和链表，可以实现一个多写多读的队列。

### 无锁栈

无锁栈比无锁队列的实现更简单，只需要对 head 指针进行 CAS 操作，就能实现多线程的入栈和出栈。

### 无锁链表

相比于无锁队列与无锁栈，无锁链表要复杂的多，因为无锁链表要在中间插入和删除元素。


