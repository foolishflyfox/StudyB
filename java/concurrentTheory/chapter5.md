# 并发容器

## BlockingQueue

在所有的并发容器中，BlockingQueue 是最常见的一种。BlockingQueue 是一个带阻塞功能的队列，当入队列时，若队列已满，则阻塞调用者；当出队列时，若队列位空，则阻塞调用者。

在 Concurrent 包中，BlockingQueue 是一个接口，有许多不同的实现类。

- ArrayBlockingQueue
- LinkedBlockingQueue
- PriorityBlockingQueue
- DelayQueue
- SynchronousQueue

该接口的定义如下：
```java
public interface Queue<E> extends Collection<E> {
    boolean add(E e);
    boolean offer(E e);
    E remove();
    E poll();
    E element();
    E peek();
}

public interface BlockingQueue<E> extends Queue<E> {
    boolean add(E e);
    boolean offer(E e);
    void put(E e) throws InterruptedException;
    E take() throws InterruptedException;
    ...
}
```
可以看到，该接口和JDK集合包中的 Queue 接口是兼容的，同时在其基础上增加了阻塞功能。在这里，入队提供了 add、offer、put 三个函数，有什么区别呢？

add 和 offer 的返回值是布尔类型，而 put 无返回值，还会抛出中断异常，所以 add 和 offer 是无阻塞的，也是 Queue 本身定义的接口。而 put 是阻塞式的。add 和 offer 的区别不大，当队列为满时，前者会抛出异常，后者则直接返回 false。

出队列与之类似，提供了 remove、peek 和 take 等函数，remove 和 peek 是非阻塞式的，take 是阻塞式的。下面分别介绍 BlockingQueue 的各种不同的实现。

### ArrayBlockingQueue

ArrayBlockingQueue 是一个用数组实现的环形队列，在构造函数中，会要求传入数组的容量。

```java
public ArrayBlockingQueue(int capacity, boolean fair) {
    ...
}
```
其核心数据结构如下：
```java
public class ArrayBlockingQueue<E> extends AbstractQueue<E>
        implements BlockingQueue<E>, java.io.Serializable {

    // 数组及队头、队尾指针
    final Object[] items;

    /** items index for next take, poll, peek or remove */
    int takeIndex;

    /** items index for next put, offer, or add */
    int putIndex;

    // 队列中元素数量
    int count;

    /*
     * Concurrency control uses the classic two-condition algorithm
     * found in any textbook.
     */

    /** Main lock guarding all access */
    final ReentrantLock lock;

    /** Condition for waiting takes */
    private final Condition notEmpty;

    /** Condition for waiting puts */
    private final Condition notFull;
```
其 put / take 函数也很简单：
```java
    public void put(E e) throws InterruptedException {
        checkNotNull(e);
        final ReentrantLock lock = this.lock;
        lock.lockInterruptibly();
        try {
            while (count == items.length)
                notFull.await();
            enqueue(e);
        } finally {
            lock.unlock();
        }
    }
    private void enqueue(E x) {
        // assert lock.getHoldCount() == 1;
        // assert items[putIndex] == null;
        final Object[] items = this.items;
        items[putIndex] = x;
        if (++putIndex == items.length)
            putIndex = 0;
        count++;
        notEmpty.signal();
    }
    public E take() throws InterruptedException {
        final ReentrantLock lock = this.lock;
        lock.lockInterruptibly();
        try {
            while (count == 0)
                notEmpty.await();
            return dequeue(e);
        } finally {
            lock.unlock();
        }
    }
    private E dequeue() {
        final Object[] items = this.items;
        @SupressWarnings("unchecked")
        E x = (E) items[takeIndex];
        items[takeIndex] = null;  // 编译 GC 的垃圾回收
        if (++takeIndex == items.length)
            takeIndex = 0;
        count--;
        notFull.signal();
        return x;
    }
```

### LinkedBlockingQueue

LinkedBlockingQueue 是一种基于单向链表的阻塞队列。因为队头和队尾是2个指针分开操作，所以用了2把锁+2个条件，同时由一个 AtomicInteger 的原子变量记录 count 数。

```java
public class LinkedBlockingQueue<E> extends AbstractQueue<E>
        implements BlockingQueue<E>, java.io.Serializable {
    private final int capacity;
    private final AtomicInteger count = new AtomicInteger();
    transient Node<E> head;
    private transient Node<E> last;
    private final ReentrantLock takeLock = new ReentrantLock();
    private final Condition notEmpty = takeLock.newCondition();
    private final ReentrantLock putLock = new ReentrantLock();
    private final Condition notFull = putLock.newCondition();

    static class Node<E> {
        E item;
        Node<E> next;
        Node(E x) { item = x; }
    }
    public LinkedBlockingQueue(int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException();
        this.capacity = capacity;
        last = head = new Node<E>(null);
    }
}
```
再其构造函数中，也可以指定队列的总容量，如果不指定，默认为 Integer.MAX_VALUE。

下面看一下其 put/take 的实现：
```JAVA
public void put(E e) throw InterruptedException {
    if (e == null) throw new NullPointException();
    Node<E> node = new Node<>(e);
    final ReentrantLock putLock = this.putLock;
    final AtomicInteger count = this.count;
    putLock.lockInterruptibly();
    try {
        while (count.get() == capacity) {
            notFull.await();
        }
        enqueue(node);
        c = count.getAndIncrement();
        if (c + 1 < capacity) 
            notFull.signal();
    } finally {
        putLock.unlock();
    }
    if (c == 0) 
        signalNotEmpty();
}
public E take() InterruptedException {
    E x;
    int c = -1;
    final AtomicInteger count = this.count;
    final ReentrantLock takeLock = this.takeLock;
    takeLock.lockInterruptibly();
    try {
        while (count.get() == 0) {
            takeLock.await();
        }
        x = dequeue();
        c = count.getAndDecrement();
        if (c > 1) {
            notEmpty.signal();å
        }
    } finally {
        takeLock.unlock();
    }
    if (c == capacity) {
        signalNotFull();
    }
    return x;
}
private void enqueue(Node x) {
    last = last.next = node;
}
private E dequeue() {
    Node<E> h = head;
    Node<E> first = next;
    h.next = h;  // 用于 GC
    head = first;
    E x = first.item;
    first.item = null;
    return x;
}
```
LinkedBlockingQueue 和 ArrayBlockingQueue 的实现有一些差异，有几点要特别说明：
1. 为了提高并发度，用2把锁，分别控制队头、队尾的操作。意味着在 put 和 put 之间、take 和 take 之间是互斥的，put 和 take 之间并不互斥。但对于 count 变量，双方都需要操作，所以必须是原子类型。
2. 因为各自拿了一把锁，所以当需要调用对方的 condition 的 signal 时，还必须加上对方的锁，就是 signalNotEmpty 和 signalNotFull 函数。示例如下：
```java
private void signalNotEmpty() {
    final ReentrantLock takeLock = this.takeLock;
    takeLock.lock();
    try {
        notEmpty.signal();
    } finally {
        takeLock.unlock();
    }
}
private void signalNotFull() {
    final ReentrantLock putLock = this.putLock;
    putLock.lock();
    try {
        notFull.signal();
    } finally {
        putLock.unlock();
    }
}
```
不仅 put 会通知 take，take 也会通知 put。当 put 发现非满的时候，会通知其他 put 线程；当 take 发现非空的时候，会通知其他 take 线程。

### PriorityBlockingQueue

队列通常是先进先出的，而 PriorityQueue 是按照元素的优先级从小到大出队列的。正因为如此，PriorityQueue 中的 2 个元素之间需要可以比较大小，并实现 Comparable 接口。

其核心数据结构如下：
```JAVA
public class PriorityBlockingQueue<E> extends AbstractQueue<E>
        implements BlockingQueue<E>, java.io.Serializable {
    ...
    private transient Object[] queue;  // 用数组实现二叉小根堆
    private transient int size;
    private transient Comparator<? super E> comparator;
    // 1 把锁 + 1 个条件，没有非满的条件
    private final ReentrantLock lock;
    private final Condition notEmpty;
}
```
其构造函数如下，如果不指定初始大小，内部会设置一个默认值 11，当元素个数超过这个大小之后，会自动扩容。
```java
    private static final int DEFAULT_INITIAL_CAPACITY = 11;
    public PriorityBlockingQueue() {
        this(DEFAULT_INITIAL_CAPACITY, null);
    }
    public PriorityBlockingQueue(int initialCapacity,
                                 Comparator<? super E> comparator) {
        if (initialCapacity < 1)
            throw new IllegalArgumentException();
        this.lock = new ReentrantLock();
        this.notEmpty = lock.newCondition();
        this.comparator = comparator;
        this.queue = new Object[initialCapacity];
    }
```
下面是对应的 pub/take 函数的实现。
```java
    public void put(E e) {
        offer(e); // never need to block
    }
    public boolean offer(E e) {
        if (e == null)
            throw new NullPointerException();
        final ReentrantLock lock = this.lock;
        lock.lock();
        int n, cap;
        Object[] array;
        while ((n = size) >= (cap = (array = queue).length))
            tryGrow(array, cap);
        try {
            Comparator<? super E> cmp = comparator;
            if (cmp == null)
                siftUpComparable(n, e, array);
            else
                siftUpUsingComparator(n, e, array, cmp);
            size = n + 1;
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
        return true;
    }

    public E take() throws InterruptedException {
            final ReentrantLock lock = this.lock;
        lock.lockInterruptibly();
        E result;
        try {
            while ( (result = dequeue()) == null)
                notEmpty.await();
        } finally {
            lock.unlock();
        }
        return result;
    }
    private E dequeue() {
        int n = size - 1;
        if (n < 0)
            return null;
        else {
            Object[] array = queue;
            E result = (E) array[0];
            E x = (E) array[n];
            array[n] = null;
            Comparator<? super E> cmp = comparator;
            if (cmp == null)
                siftDownComparable(0, x, array, n);
            else
                siftDownUsingComparator(0, x, array, n, cmp);
            size = n;
            return result;
        }
    }
```
从上面可以看到，在阻塞实现方面，和 ArrayBlockingQueue 的机制相似，主要的区别是用数组实现了一个二叉堆，从而实现了按优先级从小到大出队列。另一个区别是没有 notFull 条件，但元素个数超过数组长度时，执行扩容操作。

### DelayQueue

DelayQueue 即延迟队列，也就是一个按延迟时间从小到大出队列的 PriorityQueue。所谓延迟时间，就是`“未来将要执行的时间”-“当前时间”`。为此，放入 DelayQueue 中的元素，必须实现 Delayed 接口，如下所示：
```java
public interface Delayed extends Comparable<Delayed> {
    long getDelay(TimeUnit unit);
}
```
关于该接口，有两点说明：
1. 如果 getDelay 的返回值小于或等于0，则说明该元素到期，需要从队列中拿出来执行；
2. 该接口首先继承了 Comparable 接口，所以要实现该接口，必须实现 Comparable 接口。具体来说，就是基于 getDelay() 的返回值比较两个元素的大小。

下面看一下 DelayQueue 的核心数据结构：
```java
public class DelayQueue<E extends Delayed> extends AbstractQueue<E> implements BlockingQueue<E> {
    ...
    private final PriorityQueue<E> q = new PriorityQueue<E>();
    // 1 把锁 + 1 个非空条件
    private final transient ReentrantLock lock = new ReentrantLock();
    private final Condition available = lock.newCondition();
    ...
}
```
下面介绍 put/take 的实现，先从 take 说起，因为这样更能看出 DelayQueue 的特性。
```java
public E take() throws InterruptedException {
    final ReentrantLock lock = this.lock;
    lock.lockInterruptibly();
    try {
        for (;;) {
            E first = q.peek();  // 取出二叉堆的堆顶元素，也就是延迟时间最小的
            if (first == null) 
                available.await();  // 队列为空，take 线程阻塞
            else
                long delay = first.getDelay(NANOSECONDS);
                if (delay <= 0)
                    return poll();
                first = null;
                if (leader != null)  // 如果已经有其他线程在等待这个元素，则无限期阻塞
                    available.await();
                else{
                    Thread thisThread = Thread.currentThread();
                    leader = thisThread;
                    try {
                        available.awaitNanos(delay);  // 否则阻塞有限的时间
                    } finally {
                        if (leader == thisThread)
                            leader = null;
                    }
                }
        }
    } finally {
        if (leader == null && q.peek() != null)
            available.signal();  // 自己是 leader，已经获取了堆顶元素，唤醒其他线程
            lock.unlock();
    }
}
```
关于 take() 函数，有2点需要说明：
1. 不同于一般的阻塞队列，只在队列为空的时候才阻塞。如果堆顶元素的延时没到，也会阻塞；
2. 在上面的代码中使用了一个优化技术，用一个 Thread leader 变量记录了等待堆顶元素的第一个线程？为什么这样做呢？通过 getDelay() 可以知道堆顶元素何时到期，不必无限期等待，可以使用 condition.awaitNanos() 等待一个有限的时间；只有当发现还有其他线程也在等待堆顶元素(leader!=NULL)时，才需要无限期等待。

下面看一下 put 的实现：
```java
public void put(E e) {
    offer(e);
}
public boolean offer(E e) {
    final ReentrantLock lock = this.lock;
    lock.lock();
    try {
        q.offer(e);
        if (q.peek() == e) {
            // 如果放进去的元素刚好在堆顶，说明放入的元素延迟时间是最小的，那么需要通知等待的线程
            // 否则放入的元素不在堆顶，没有必要通知等待的线程
            leader = null;
            available.signal();
        }
        return true;
    } finally {
        lock.unlock();
    }
}
```

### SynchronousQueue

SynchronousQueue 是一种特殊的 BlockingQueue，它本身没有容量。先调用 put，线程会阻塞；直到另一个线程调用了 take，两个线程才同时解锁，反之亦然。

对于多个线程而言，例如3个线程，调用 3 次 put()，3个线程都会阻塞；其他线程每调用一次，都有一个线程被解锁。

在讲解线程池中的 CachedThreadPool 实现的时候会使用到 SynchronousQueue 的这种特性。

接下来看一下，SynchronousQueue 是如何实现的。先从构造函数看起。

```java
public SynchronouseQueue(boolean fair) {
    transferer = fair ? new TransferQueue() : new TransferStack();
}
```
和锁一样，也有公平和非公平模式。如果是公平模式，则用 TransferQueue 实现；如果是非公平模式，则用 TransferStack 实现。这两个类分别是什么呢？先看一下 put/take 的实现。
```java
public void put(E o) throws InterruptedException {
    if (o == null) throw new NullPointerException();
    if (transferer.transfer(o, false, 0) == null) {
        Thread.interrupted();
        throw new InterruptedException();
    }
}
public E take() throws InterruptedException {
    E e = transferer.transfer(null, false, 0);
    if (e != null) 
        return e;
    Thread.interrupted();
    throw new InterruptedException();
}
```
可以看到，put/take 都调用了 transfer 接口。而 TransferQueue 和 TransferStack 分别实现了这个接口。这个接口在 SynchronousQueue 内部，如下所示。如果是 put，则第一个参数就是对应的元素，如果是take，则第一个参数就是 null。后面两个参数分别为：是否设置超时和对应的超时时间。
```java
    abstract static class Transferer<E> {
        abstract E transfer(E e, boolean timed, long nanos);
    }
```

## BlockingDeque

BlockingDeque 定义了一个阻塞的双端队列接口。
```java
public interface BlockingDeque<E> extends BlockingQueue<E>, Deque<E> {
    void addFirst(E e);
    void addLast(E e);
    boolean offerFirst(E e);
    boolean offerLast(E e);
    void putFirst(E e) throws InterruptedException;
    void putLast(E e) throws InterruptedException;
    boolean offerFirst(E e, long timeout, TimeUnit unit)
        throws InterruptedException;
    boolean offerLast(E e, long timeout, TimeUnit unit)
        throws InterruptedException;
    E takeFirst() throws InterruptedException;
    E takeLast() throws InterruptedException;
}
```
对应的实现原理和LinkedBlockingQueue 基本一样，只是 LinkedBlockingQueue 是单向链表，而 LinkedBlockingDeque 是双向链表。

## CopyOnWrite

CopyOnWrite 指在写的时候，不是直接写源数据，而是把数据拷贝一份进行修改，再通过悲观锁或乐观锁的方式写回去。那为什么不直接修改，而是要拷贝一份修改呢？这是为了在读的时候不加锁。下面通过几个案例展示 CopyOnWrite 的应用。

### CopyOnWriteArrayList

和 ArrayList 一样，CopyOnWriteArrayList 的核心数据结构也是一个数组，代码如下：
```java
public class CopyOnWriteArrayList<E>
        implements List<E>, RandomAccess, Cloneable, java.io.Serializable {
    private transient volatile Object[] array;
    ...
}
```
下面是 CopyOnArrayList 的几个“读”函数：
```java
    final Object[] getArray() {
        return array;
    }
    public E get(int index) {
        return (E)(getArray()[index]);
    }
    public boolean contains(Object o) {
        Object[] elements = getArray();
        return indexOf(o, elements, 0, elements.length) >= 0;
    }
    public int indexOf(Object o) {
        Object[] elements = getArray();
        return indexOf(o, elements, 0, elements.length);
    }
    public static int indexOf(Object o, Object[] elements, int index, int fence) {
        if (o == null) {
            for (int i=index; i <  fence; i++) {
                if (null == elements[i]) {
                    return i;
                }
            }
        } else {
            for (int i=index; i < fence; i++) {
                if (o.equals(elements[i])) {
                    return i;
                }
            }
        }
        return -1;
    }
```
既然这些读函数都没有加锁，那么如何保障线程安全呢？答案是在写函数里面。
```java
final void setArray(Object[] a) {
    array = a;
}
public boolean add(E e) {
    final ReentrantLock lock = this.lock;
    lock.lock();
    try {
        Object[] element = getArray();
        int len = elements.length;
        Object[] newElements = Arrays.copyOf(element, len+1);
        // CopyOnWrite 写的时候，先拷贝一份之前的数组
        newElements[len] = e;
        setArray(newElements);
        return true;
    } finally {
        lock.unlock();
    }
}
```
其他写函数，例如 remove 和 add 类似，此处不再详述。

### CopyOnWriteArraySet
CopyOnWriteArraySet 就是用 Array 实现的一个 Set，保证所有元素都不重复。其内部封装的是一个 CopyOnWriteArrayList。

```java
public class CopyOnWriteArraySet<E> extends AbstractSet<E> implements java.io.Serializable {
    private final CopyOnWriteArrayList<E> al;
    public CopyOnWriteArraySet() {
        al = new CopyOnWriteArrayList<E>();
    }
    public boolean add(E e) {
        return al.addIfAbsent(e);
    }
}
```

## ConcurrentLinkedQueue / Deque

前面详细分析了 AQS 内部的阻塞队列实现原理：基于双向链表，通过对 head / tail 进行 CAS 操作，实现入队和出队。

ConcurrentLinkedQueue 的实现原理和 AQS 内部的阻塞队列类似：同样是基于 CAS，同样是通过 head/tail 指针记录队列头部和尾部，但还是有稍许差别。

首先，它是一个单向链表，定义如下：
```java
public class ConcurrentLinkedQueue<E> extends AbstractQueue<E> 
    implements Queue<E>, java.io.Serializable {
        private static class Node<E> {
            volatile E item;
            volatile Node<E> next;
        }
        private transient volatile Node<E> head;
        private transient volatile Node<E> tail;
    }
```
其次，在 AQS 的阻塞队列中，每次入队列后，tail 一定后移一个位置；每次出队，head 一定前移一个位置，以保证 head 指向队列头部，tail 指向队列尾部。

但在 ConcurrentLinkedQueue 中，head/tail 的更新可能落后于节点的入队和出队，因为它不是直接对 head / tail 指针进行 CAS 操作，而是对 Node 中的 item 进行操作。


## ConcurrentHashMap

HashMap 通常的实现方式是 “数组+链表”，这种方式被称为拉链法。ConcurrentHashMap 在这个基本原理之上进行了各种优化，在 JDK7 和 JDK8 中的实现方式有很大的差异。

### JDK 7 中的实现

为了提高并发度，在 JDK7 中，一个 HashMap 被拆分为多个子 HashMap。每个子 HashMap 称为一个 Segment，多个线程操作多个 Segment 相互独立。

## ConcurrentSkipListMap/Set

ConcurrentHashMap 是一种 key 无序的 HashMap，HashMap 则是 key 有序的，实现了 NavigableMap 接口，此接口又继承了 SortedMap 接口。

在 Java 的 util 包中，有一个非线程安全的 HashMap，也就是 TreeMap，是 key 有序的，基于红黑树实现的。而在 Concurrent 包中，提供的 key 有序的 HashMap 也就是 ConcurrentSkipListMap 是基于 SkipList (跳查表) 来实现的。
