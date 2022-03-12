# Map

- 线程安全
    - ConcurrentHashMap
    - SynchronizedMap
- 非线程安全
    - HashMap
    - IdentityHashMap

## ConcurrentHashMap vs SynchronizedMap

- ConcurrentHashMap: 线程安全，将其整个 Hash 桶进行了分段 segment，也就是将这个大的数组分成几个小片段 segment，而且每个小的片段 segment 上都有锁存在，那么在插入元素的时候就要先找到该插入到哪个片段 segment，然后在该片段上进行插入，而且这里还需要获取 segment 锁(即锁分段技术)；ConcurrentHashMap 让锁的粒度更加精细，并发性更好；
- SynchroinzedMap: 线程安全；通过synchronized关键字进行同步控制；所有单个的操作都是线程安全的，但是多个操作组成的操作序列却可能导致数据争用，因为在操作序列中控制流取决于前面操作的结果。这也被称作是：有条件的线程安全性；
效率低；

## IdentityHashMap vs HashMap

HashMap 的 hash() 方法为：
```java
    static final int hash(Object key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }
```
`hashCode()` 是 Object 上的一个 native 方法，用户可以实现自定义。当两个对象执行 equals 返回 true，那么他们的 hashCode() 返回必定相等。

`>>>` 无符号右移，忽略符号位，空位补0；

IdentityHashMap 的 hash() 方法为：
```java
private static int hash(Object x, int length) {
        int h = System.identityHashCode(x);
        // Multiply by -127, and left-shift to use least bit as part of hash
        return ((h << 1) - (h << 8)) & (length - 1);
    }
```
两者的差异如下面的例子所示：
```java
public class T4 {
    static class Twins {
        String color;
        public Twins(String color) {
            this.color = color;
        }
        public void setColor(String color) {
            this.color = color;
        }
        @Override
        public boolean equals(Object otherObj) {
            if (this == otherObj) {
                return true;
            }
            if (!(otherObj instanceof Twins)) {
                return false;
            }
            return Objects.equals(color, ((Twins) otherObj).color);
        }
        @Override
        public int hashCode () {
            return Objects.hashCode(color);
        }
        @Override
        public String toString() {
            return String.format("Twins(%s)", color);
        }
    }
    public static void main(String[] args) {
        HashMap<Twins, Integer> map1 = new HashMap<>();
        HashMap<Twins, Integer> map2 = new HashMap<>();
        IdentityHashMap<Twins, Integer> map3 = new IdentityHashMap<>();
        IdentityHashMap<Twins, Integer> map4 = new IdentityHashMap<>();

        Twins t1 = new Twins("red");  map1.put(t1, 10);
        Twins t2 = new Twins("red");  map1.put(t2, 20);
        System.out.println(map1);  // {Twins(red)=20} , HashMap 的 key 使用 hashCode 进行比较

        Twins t3 = new Twins("red");  map2.put(t3, 10);
        t3.setColor("green");  map2.put(t3, 20);
        System.out.println(map2);  // {Twins(green)=10, Twins(green)=20}

        Twins t4 = new Twins("red");  map3.put(t4, 10);
        Twins t5 = new Twins("red");  map3.put(t5, 20);
        System.out.println(map3);  // {Twins(red)=10, Twins(red)=20} ，使用引用比较

        Twins t6 = new Twins("red");  map4.put(t6, 10);
        t6.setColor("green");  map4.put(t6, 20);
        System.out.println(map4);  // {Twins(green)=20}
    }
}
```
结论：在存放例如 String、Integer 等对象作为 key 的时候，使用 HashMap。如果是对象中还包括了成员变量，并且我们明确希望保存的key是对象引用的时候，使用 IdentityHashMap 。

## ConcurrentHashMap

参考：![彻头彻尾理解 ConcurrentHashMap](https://blog.csdn.net/justloveyou_/article/details/72783008)



