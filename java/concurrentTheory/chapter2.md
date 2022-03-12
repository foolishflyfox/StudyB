# Atomic 类

从本章开始，我们将从简单到复杂，从底层到上层，一步步剖析整个 Concurrent 包的层次体系。

- CompletableFuture
- 线程池、Future、ForkJoinPool
- 并发容器(BlockingQueue、ConcurrentHashMap 等)
- 同步工具(信号量、CountDownLatch)
- 锁与条件(互斥锁，多写锁)
- Atomic 类

## AtomicInteger 和 AtomicLong

如下代码所示，对于一个整数的加减操作，要保证线程安全，需要加锁，也就是加 synchronized 关键字。
```java
public class Example {
    private int count = 0;
    public void synchronized increment() {
        count++;  // 线程 A 调用
    }
    public void synchronized decrement() {
        count--;  // 线程 B 调用
    }
}
```
但有了 Concurrent 包的 Atomic 相关类之后，synhronized 关键字可以用 AtomicInteger 替代，其性能更好，对应的代码变为：
```java
public class Example {
    private AtomicInteger count = new AtomicInteger(0);
    public void increment() {
        count.getAndIncrement();
    }
    public void decrement() {
        count.getAndDecrement();
    }
}
```
其对应的源码如下：
```java
public class AtomicInteger extends Number implements java.io.Serializable {
    ...
    private volatile int value;  // 封装了一个 int 变量，对其进行 CAS 操作
    ...
    public final int getAndIncrement() {
        for (;;) {
            int current = get();
            int next = current + 1;
            if (compareAndSet(current, next)) {
                return current;
            }
        }
    }
    public final int getAndDecrement() {
        for (;;) {
            int current = get();
            int next = current - 1;
            if (compareAndSet(current, next)) {
                return current;
            }
        }
    }
}
```
其源码很简单，但却反映了几个很重要的思想，下面一一说明。

### 悲观锁与乐观锁

对于悲观锁，作者认为数据发生并发冲突的概率很大，所以读操作之前就上锁。synchronized 关键字，以及后面要将的 ReentrantLock 都是悲观锁的典型例子。

对于乐观锁，作者认为数据发生并发冲突的概率比较小，所以读操作之前不上锁，等到写操作的时候，再判断数据在此期间是否被其他线程修改了。如果被其他线程修改了，就把数据重新读取出来，重复该过程；如果没有被修改，就写回去。判断数据是否被修改，同时写回新值，这两个操作要合成一个原子操作，也就是CAS(Compare And Set)。AtomicInteger 的实现就是典型的乐观锁，在 MySQL 和 Redis 中有类似的思路。

### Unsafe 的 CAS 详解

上面调用的 CAS 函数，其实是封装的 Unsafe 类中的 native 函数，如下所示：
```java
public final boolean compareAndSet(int expect, int update) {
    return unsafe.compareAndSwapInt(this, valueOffset, expect, update);
}
```
Unsafe 类是整个 Concurrent 包的基础，里面所有的函数都是 native 的，具体到 compareAndSwapInt 函数，如下所示：
```java
public final native boolean compareAndSwapInt(Object var1, long var2, int var4, int var5);
```
该函数有4个参数：在前两个参数中，第一个是对象(也就是 AtomicInteger 对象)，第二个是对象的成员变量(也就是 AtomicInteger 里面包的 int 变量 value)，后两个参数保持不变。

要特别说明一下第二个参数，它是一个 long 类型的整数，经常被称为 xxxOffset，意思是某个成员变量在对应的类中的内存偏移量(该变量在内存中的位置)，表示该成员变量本身。在 Unsafe 中专门有一个函数，将成员变量转化为偏移量，如下所示：
```java
public native long ObjectFieldOffset(Field var1);
```
所有调用 CAS 的地方，都会先通过这个函数变量转换为一个 Offset。以 AtomicInteger 为例：
```java
public class AtomicInteger extends Number implements java.io.Serializable {
    ...
    private static final Unsafe unsafe = Unsafe.getUnsafe();
    private static final long valueOffset;
    static {
        try {
            valueOffset = unsafe.ObjectFieldOffset(AtomicInteger.class.getDeclaredField("value"));
        } catch (Exception ex) {
            throw new Error(ex);
        }
    }
    ...
}
```
从上面代码可以看到，无论是 Unsafe 还是 valueOffset，都是静态的，也就是类级别的，所有对象共用的。

在转换的时候，先通过反射(getDeclaredField)获取 value 成员变量对应的 Field 对象，再通过 objectFieldOffset 函数转换成 valueOffset。此处的 valueOffset 就代表了 value 变量本身，后面执行 CAS 操作的时候，不是直接操作 value，而是操作 valueOffset。

### 自旋与阻塞

当一个线程拿不到锁的时候，有两种基本的等待策略。
策略1：放弃 CPU，进入阻塞状态，等待后续被唤醒，在重新被操作系统调度；
策略2：不放弃 CPU，空转，不断重试，也就是所谓的“自旋”；

很显然，如果是单核 CPU，只能用策略1.因为如果不放弃 CPU，那么其他线程无法运行，也就无法释放锁。但对于多 CPU 或者多核，策略2就很有用了，因为没有线程切换的开销。

AtomicInteger 的实现就用的是“自旋”策略，如果拿不到锁，就会一直重试。

有一点要说明：这两种策略并不是互斥的，可以结合使用。如果拿不到锁，先自旋几圈；如果自旋还拿不到锁，再阻塞，synchronized 关键字就是这样的实现策略。除了 AtomicInteger，AtomicLong 也是同样的原理，此处不再赘述。

## AtomicBoolean 和 AtomicReference

### 为什么需要 AtomicBoolean

对于 int 或者 long 类型变量，需要进行加减操作，所以要加锁。但对于一个 boolean 类型来说，true 或者 false 的赋值和取值操作，加上 volatile 关键字就够了，为什么还要 AtomicBoolean ？

这是因为往往要上线下面这种功能：
```java
if (flag == false) {
    flag = true;
    ...
}
```
也就是要上线 compare 和 set 两个操作合在一起的原子性，而这也正是 CAS 提供的功能。上面的代码就变成了：
```java
if (compareAndSet(false, true)) {
    ...
}
```
同样地，AtomicReference 也需要同样的功能，对应的函数如下：
```java
    public final boolean compareAndSet(V expect, V update) {
        return unsafe.compareAndSwapObject(this, valueOffset, expect, update);
    }
```
其中，expect 是旧的引用，update 为新的引用。

### 如何支持 boolean 和 double 类型
在 Unsafe 类中，只提供了三种类型的 CAS 操作：int、long 和 Object(也就是引用类型)。如下所示：
```java
public final class Unsafe {
    public final native boolean compareAndSwapObject(Object var1, long var2, Object var4, Object var5);
    public final native boolean compareAndSwapInt(Object var1, long var2, int var4, int var5);
    public final native boolean compareAndSwapLong(Object var1, long var2, long var4, long var6);
}
```
第一个参数是要修改的对象，第二个参数是对象的成员变量在内存中的位置(一个long型的整数)，第三个参数是该变量的旧值，第四个参数是该变量的新值。

AtomicBoolean 类型怎么支持呢？

对于用 int 型来替代的，在入参的时候，将 boolean 类型转换成 int 类型；在返回值的时候，将 int 类型转换成 boolean 类型。
```java
public class AtomicBoolean implements java.io.Serializable {
    ...
    public volatile int value;
    public final boolean get() {
        return value!=0;
    }
    public final boolean compareAndSet(boolean expect, boolean update) {
        int e = expect ? 1 : 0;
        int u = update ? 1 : 0;
        return unsafe.compareAndSwapInt(this, valueOffset, e, u);
    }
}
```
如果是 double 类型，又如何支持呢？

这依赖 double 类型提供的一堆 double 类型和 long 类型相互转换的函数，这点在介绍 DoubleAdder 的时候会提到。
```java
public final class Double extends Number implements Comparable<Double> {
    ...
    public static native double longBitsToDouble(long bits);
    public static native long doubleToRawLongBits(double value);
}
```

## AtomicStampedReference 和 AtomicMarkableReference

### ABA 问题与解决办法

到目前为止，CAS 都是基于“值”来做比较的。但如果另外一个线程把变量的值从 A 改为 B，再从 B 改回 A，那么尽管修改过两次，可是在当前线程做 CAS 操作的时候，却会因为值没变而认为数据没有被其他线程修改过，这就是所谓的 ABA 问题。

要解决 ABA 问题，不仅要比较值，还要比较版本号，这正是 AtomicStampedReference 做的事情，对应的 CAS 函数为：
```java
public boolean compareAndSet(V expectedReference, V newReference, int expectedStamp, int newStamp)
```
之前的 CAS 只有两个参数，这里CAS有4个参数，后两个参数就是版本号的旧值和新值。

当 expectedReference != 对象的 reference 时，说明该数据肯定被其他线程修改过；当 expectedReference == 对象的 reference 时，进一步比较 expectedStamp 是否等于对象当前的版本号，以此判断数据是否被其他线程修改过。

### 为什么没有 AtomicStampedInteger 或 AtomicStampedLong

因为这里要同时比较数据的值和版本号，而 Integer 型或 Long 型的 CAS 没办法同时比较两个变量，于是只能将值和版本号封装成一个对象，也就是这里面的 Pair 内部类，然后通过对象引用的 CAS 来实现。代码如下：
```java
public class AtomicStampedReference<V> {
    private static class Pair<T> {
        final T reference;
        final int stamp;
        private Pair(T reference, int stamp) {
            this.reference = reference;
            this.stamp = stamp;
        }
        static<T> Pair<T> of(T reference, int stamp) {
            return new Pair<T>(reference, stamp);
        }
    }
    private volatile Pair<V> pair;
    public boolean CompareAndSet(V expectedReference, V newExpectedReference, int expectedStamp, int newStamp) {
        Pair<V> current = pair;
        return expectedReference == current.reference &&
            expectedStamp == current.stamp &&
            ((newReference == current.reference && newStamp == currentStamp) ||
                casPair(current, Pair.of(newReference, newStamp)));
    }
    private boolean casPair(Pair<V> cmp, pair<V> val) {
        return UNSAFE.compareAndSet(this, pairOffset, cmp, val);
    }
    ...
}
```
当使用的时候，在构造函数中传入值和版本号两个参数，应用程序对版本号进行累加操作，然后调用上面的 CAS。如下所示：
```java
public AtomicStampedReference(V initialRef, int initalStamp) {
    pair = Pair.of(initialRef, initialStamp);
}
```
### AtomicMarkableReference

AtomicMarkableReference 与 AtomicStampedReference 原理类似，只是 Pair 里面的版本号是 boolean，不是整型的累加变量，如下所示：
```java
public class AtomicMarkableReference<V> {
    private static class Pair<T> {
        final T reference;
        final boolean mark;
        private Pair(T reference, boolean mark) {
            this.reference = reference;
            this.mark = mark;
        }
        static<T> Pair<T> of(T reference, boolean mark) {
            return new Pair<T>(reference, mark);
        }
    }
    private volatile Pair<V> pair;
    ...
}
```
因为是 boolean 类型，只能有 true、false 两个版本，所以并不能完全避免 ABA 问题，只是降低了 ABA 发生的概率。

## 2.4 AtomicIntegerFieldUpdater / AtomicLongFieldUpdater / AtomicReferenceFieldUpdater

### 2.4.1 为什么需要 AtomicXXXFieldUpdater

如果一个类是自己编写的，则可以在编写的时候把成员变量定义为 Atomic 类型。但如果是一个已有的类，在不能更改其源代码的情况下，要想实现对其成员变量的原子操作，就需要 AtomicIntegerFieldUpdater / AtomicLongFieldUpdater / AtomicReferenceFieldUpdater。

下面以 AtomicIntegerFieldUpdater 为例，介绍其实现原理：首先，其构造函数是 protected，不能直接构造其对象，必须通过它提供的一个静态函数来创建：
```java
public abstract class AtomicIntegerFieldUpdater<T> {
        @CallerSensitive
    public static <U> AtomicIntegerFieldUpdater<U> newUpdater(Class<U> tclass,
                                                              String fieldName) {
        ... 
    }
}
```
newUpdater(...) 静态函数传入的是要修改的类(不是对象)和对应的成员变量的名字，内部通过反射拿到这个类的成员变量，然后包装成一个 AtomicIntegerFieldUpdater 对象。所以，这个对象表示的是类的某个成员，而不是对象的成员变量。

若要修改某个对象的成员变量的值，再传入相应的对象，如下所示：、
```java
public int getAndIncrement(T obj) {
    int prev, next;
    do {
        prev = get(Obj);
        next = prev + 1;
    } while (!compareAndSet(obj, prev, next));
    return prev;
}
public final boolean compareAndSet(T obj, int expect, int update) {
    accessCheck(obj);
    return U.compareAndSwapInt(obj, offset, expect, update);
}
```
accessCheck 函数的作用是检查该 obj 是不是 tclass 类型，如果不是，则拒绝修改。

### 限制条件

要想使用 AtomicIntegerFieldUpdater 修改成员变量，成员变量必须是 volatile 的 int 类型，不能是 Integer 包装类型。从其构造函数中可以看到：
```java
AtomicIntegerFieldUpdaterImpl(final Class<T> tclass,
                                final String fieldName,
                                final Class<?> caller) {
    ...

    if (field.getType() != int.class)
        throw new IllegalArgumentException("Must be integer type");

    if (!Modifier.isVolatile(modifiers))
        throw new IllegalArgumentException("Must be volatile type");

    ...
}
```
## Striped64 与 LongAdder

从 JDK 8 开始，针对 Long 型的原子操作，Java 又提供了 LongAdder、LongAccumulator；针对 Double 类型，Java 提供了 DoubleAdder、DoubleAccumulator。这4个类都继承自 Striped64.

### LongAdder 原理

AtomicLong 内部是一个 volatile long 类型变量，由多个线程对这个变量进行 CAS 操作。多个线程同时对一个变量进行 CAS 操作，在高并发的场景下仍然不够快，如果要再提高性能，该怎么做？

把一个变量拆分为多份，变为多个变量，类似于 ConcurrentHashMap 的分段锁的例子。把一个 Long 型拆分为一个 base 变量外加多个 Cell，每个 Cell 包装了一个 Long 型变量。当多个线程并发累加时，如果并发度低，就直接加到 base 变量上；如果并发度高，冲突大，就平摊到这些 Cell 上。在最后取值的时候，再把 base 和这些 Cell 求 sum 运算。

### 最终一致性

在 sum 求和函数中，并没有对 cells[] 数组加锁。也就是说，一边有现成对其执行求和操作，一遍还有线程修改数组里面的值，也就是最终一致性，而不是强一致性。因此，在 LongAdder 开篇的注释中，将它和 AtomicLong 的场景做了比较。它适合高并发的统计场景，而不适合要对某个 Long 型变量进行严格同步的场景。

### 伪共享和缓存行填充

在 Cell 类的定义中，用了一个独特的注解 `@sun.misc.Contended`，这是 JDK 8 之后才有的，背后涉及一个很重要的优化原理：伪共享与缓存行填充。在讲 CPU 架构的时候提到过，每个 CPU 都有自己的缓存。缓存与主内存进行数据交换的基本单位叫 Cache Line(缓存行)。在 64位 x86 架构中，缓存行是64字节，也就是 8 个 Long 类型大小。这也意味着当缓存失效，要刷新到主内存的时候，最少要刷新 64 字节。

假设，主内存中有变量 X、Y、Z（假设每个变量都是一个 Long 类型），被 CPU1 和 CPU2 分别读入自己的缓存，放在同一行 Cache Line 里面。当 CPU1 修改了 X 变量，它要失效整行 Cache Line，也就是往总线上发消息，通知 CPU2 对应的 Cache Line 失效。由于 Cache Line 是数据交换的基本单元，无法只失效 X，要失效就失效整行的 Cache Line，这会导致 Y、Z 变量的缓存也失效。

虽然只修改了 X 变量，但 Y、Z 变量也随之失效。但 Y、Z 变量的数据并没有修改，本应该很好地被CPU1、CPU2共享，但却没做到，这就是所谓的“伪共享问题”。

问题的原因是，Y、Z和X变量处在了同一行 Cache Line 里面。要解决这个问题，需要用到所谓的“缓存行填充”，分别在 X、Y、Z 后面加上7个无用的 Long 类型，填充整个缓存行，让 X、Y、Z 在三行不同的缓存中。


