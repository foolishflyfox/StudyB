# 泛型程序设计

泛型类和泛型方法有类型参数，这使得它们可以准确的描述用特定类型实例化时会发生什么。在有泛型类之前，程序员必须使用 Object 编写适用于多种类型的代码，这很繁琐，也不安全。

随着泛型的引入，Java 有了一个表达能力很强的类型系统，允许设计者详细地描述变量和方法的类型要如何变化。泛型的目标是让其他程序员可以轻松使用的类和方法而不会出现意外。

Java 5 中引入泛型成为 Java 程序设计语言自最初发行以来最显著的变化。Java 的一个主要设计目标是支持与之前版本的兼容性。因此，Java 的泛型有一些让人不快的局限性。

## 为什么使用泛型程序设计

泛型程序设计(generic programming)意味着编写的代码可以对多种不同类型的对象重用。

### 类型参数的好处

在 Java 中增加泛型类之前，泛型程序设计是用继承实现的。ArrayList 类只维护一个 Object 引用的数组。
```java
public class ArrayList {  // before generic classes
    private Object[] elementData;
    ...
    public Object get(int i) { ... }
    public void add(Object o) { ... }
}
```
这种方法存在两个问题。当获取一个值时，必须进行强制类型转换：`ArrayList files = new ArrayList(); ...; String fileName = (String)files.get(0);`。

## 泛型代码和虚拟机

虚拟机没有泛型类型对象，所有对象都属于普通类。在泛型实现的早期版本中，甚至能够将使用泛型的程序编译为 1.0 虚拟机上运行的类文件。

### 类型擦除

无论何时定义一个泛型类型，都会自动提供一个相应的原始类型（raw type）。这个原始类型的名字去掉类型参数后的泛型类型名。类型变量会被擦除(erased)，并替换为其限定类型(或者，对于无限定的变量则替换为 Object)。

原始类型用第一个限定来替换类型变量，或者，如果没有给定限定，就替换为 Object。例如，类 `Pair<T>` 中的类型变量没有显式的限定，因此，原始类型用 Object 替换 T。假定我们说明了一个稍有不同的类型：
```java
public class Interval<T extends Comparable & Serializable> implements Serializable {
    private T lower;
    private T upper;
    ... ...
    public Interval(T first, T second) {
        if(first.compareTo(second) <= 0) { lower = first; upper = second;}
        else { lower = second; upper = first; }
    }
}
```
原始类型 Interval 如下：
```java
public class Interval implements Serializable {
    private Comparable low;
    private Comparable upper;
    ... ...
    public Interval(Comparable first, Comparable second) {...}
}
```
为了提高效率，应该将标签(tagging)接口(即没有方法的接口)放在限定列表的末尾。

### 转换泛型表达式

编写一个泛型方法调用时，如果擦除了返回类型，编译器会擦入强制类型转换。例如：
```java
Pair<Employee> buddies = ...;
Employee buddy = buddies.getFirst();
```
getFirst 擦除类型后的返回类型为 Object。编译器主动插入转换到 Employee 的强制类型转换。也就是说，编译器吧这个方法调用转换为两条虚拟指令：
- 对原始方法 Pair.getFirst 的调用；
- 将返回的 Object 类型强制转换为 Employee 类型。

### 转换泛型方法

类型擦除也会出现在泛型方法中。程序员通常认为类似以下的泛型方法 `public static <T extends Comparable> T min(T[] a)` 是整个一组方法，而擦除类型后，只剩下一个方法：`public static Comparable min(Comparable[] a)`。

注意，泛型参数 T 已经被移除了，只留下限定类型 Comparable 。

对于 Java 泛型的转换，需要记住以下几个事实：
- 虚拟机中没有泛型，只有普通的类和方法；
- 所有的类型参数都会被替换为它们的限定类型；
- 会合成桥方法来保持多态；
- 为了保持类型安全性，必要时会插入强制类型转换。

## 限制与局限性

大多数限制都是由于类型擦除引起的。

### 不能用基本类型实例化类型参数
不能用基本类型代替类型参数。因此，没有 `Pair<double>`，只有 `Pair<Double>`。当然，其原因就在于类型擦除。擦除之后，Pair 类含有 Object 类型字字段，而 double 值不能赋给 Object 类型。

这的确令人烦恼，但是，这样做与 Java 语言中基本类型的独立状态相一致。这并不是一个致命的缺陷——只有8种基本类型，而且，即使不能接受包装器类型(wrapper type)，也可以使用单独的类和方法来处理。

### 运行时类型查询只适用于原始类型

虚拟机中的对象总有一个特定的非泛型类型。因此，所有的类型查询只产生原始类型。例如：`if (a instanceof Pair<String>)` 是错误的，事实上，仅仅测试 a 是否是任意类型的一个 Pair 。

下面的测试同样如此：`if (a instanceof Pair<T>)`，或者强制类型转换：`Pair<String> p = (Pair<String>)a`。为提醒这一错误，如果试图查询一个对象是否属于某个泛型类型，你会得到一个编译器错误(使用 instanceof)，或者一个警告(使用强制类型转换)。

例如下面的代码：
```java
public class T1 {
    public static void main(String[] args) {
        List<Integer> a = new ArrayList<>();
        boolean v1 = a instanceof List<Integer>; // 报错：instanceof 的泛型类型不合法
        boolean v2 = a instanceof List;  // 不报错
        List<Number> b1 = a;  // 报错：不兼容的类型: List<Integer>无法转换为List<Number>
        List<Number> b2 = (List<Number>)a;  // 报错，同上
        List<Number> b3 = (List)a;  // 警告：T1.java使用了未经检查或不安全的操作。
        List<Integer> b4 = (List<Integer>)a;  // 没有告警报错
    }
}
```
### 不能创建参数化类型的数组

```java
public class T1 {
    public static void main(String[] args) {
        new Pair<String>[10];
    }

    static class Pair<T> {
        private T first;
        private T second;
    }
}
```
报错：`不是语句 new Pair<String>[10];`。这有什么问题呢？擦除之后，table 的类型是 Pair[]，可以将它转换为 Object[]：`Object[] objarray = table`。数组会记得它的元素类型，如果试图存储其他类型的元素，就会抛出 ArrayStoreException 异常。

不过对于泛型，擦除会使这种机制无效。以下赋值 `objarray[0] = new Pair<Employee>()`，尽管能够通过数组存储的检查，但仍会导致一个类型错误。出于这个原因，不允许创建参数化类型的数组。而声明类型为 `Pair<String>[]` 的变量仍是合法的。不过不能用 `new Pair<String>[10]` 初始化这个变量。

### 不能实例化类型变量

不能在类似 new T(...) 的表达式中使用类型变量，例如，下面的 `Pair<T>` 构造器就是非法的：
```java
public Pair() { first = new T(); second = new T(); } // Error
```
类型擦除将 T 变成 Object，而你肯定不希望调用 new Object()。

在 Java 8 之后，最好的解决办法是让调用者提供一个构造器表达式，例如 `Pair<String> p = Pair.makePair(String::new)`。makePair 方法接收一个 `Supplier<T>`，这是一个函数式接口，表示一个无参数而且返回类型为 T 的函数：
```java
public static Pair<T> makePair(Supplier<T> constr) {
    return new Pair<>(constr.get(), constr.get());
}
```
例如下面的例子：
```java
import java.util.function.Supplier;

public class T1 {
    public static void main(String[] args) {
        Pair<String> p1 = Pair.makePair(String::new);
        System.out.println(p1);
        Pair<String> p2 = Pair.makePair(() -> "apple");
        System.out.println(p2);
        Pair<String> p3 = Pair.makePair(()->"banana", ()->"orange");
        System.out.println(p3);
    }
    static class Pair<T> {
        final private T first;
        final private T second;
        private Pair(T first, T second) {
            this.first = first;
            this.second = second;
        } 
        public T getFirst() { return first; }
        public T getSecond() { return second; }
        public static <T> Pair<T> makePair(Supplier<T> constructor) {
            return new Pair<T>(constructor.get(), constructor.get());
        }
        public static <T> Pair<T> makePair(Supplier<T> c1, Supplier<T> c2) {
            return new Pair<T>(c1.get(), c2.get());
        }
        @Override
        public String toString() {
            return getClass().getSimpleName()+"[first=" + first + " , " + "second=" + second + "]";
        }
    }
}
```
输出结果为：
```
Pair[first= , second=]
Pair[first=apple , second=apple]
Pair[first=banana , second=orange]
```

### 不能构造泛型数组

就像不能实例化泛型实例一样，也不能实例化数组，不过原因有所不同，毕竟数组可以填充 null 值，看上去可以安全地改造。不过，数组本身也带有类型，用来监控虚拟机中的数组存储。这个类型会被擦除。例如：
```java
public static <T extends Comparable> T[] minmax(T... a) {
    T[] mm = new T[2]; // ERROR
    ... ...
}
```
类型擦除会让这个方法总是构造 `Comparable[2]` 数组。如果数组仅仅作为一个类的私有实例字段，那么可以将这个数组的元素声明为擦除的类型并使用强制类型转换。例如 ArrayList 类可以如下实现：
```java
public class ArrayList<> {
    private Object[] element;
    ... 
    @SupressWarning("unchecked")
    public E get(int n) {
        return (E) element[n];
    }
    public void set(int n, E e) {
        element[n] = e;
    }
}
```

### 泛型类的静态上下文中类型变量无效

不能在静态字段或方法中引用类型变量。例如，下面的做法看起来很聪明，但实际上想不通：
```java
public class Singleton<T> {
    private static T singleInstance;  // error
    
    public static T getSingleInstance() {
        if (null == singleInstance) construct new instance of T
        return singleInstance;
    }
}
```
如果这样可行，程序就可以声明一个 `Singleton<Random>` 共享一个随机数生成器，另外说明一个 `Singleton<JFileChooser>` 共享一个文件选择器对话框。但是，这样是不行的，类型擦除之后，只剩下 Singleton 类，它只包含一个 singleInstance 字段，因此，禁止使用带有类型变量的静态字段和方法。

## 泛型类型的继承规则

在使用泛型类时，需要了解有关继承和子类型的一些规则。下面先从许多程序员感觉不太直观的情况开始结束。考虑一个类和一个子类，如 Employee 和 Manager。`Pair<Manager>` 是 `Pair<Employee>` 的一个子类吗？答案是：否。

例如，下面的代码将不能编译成功：
```java
Manager[] topHonchos = ...;
Pair<Employee> result = ArrayAlg.minmax(topHonchos);  // Error
```
minmax 返回 `Pair<Manager>`，而不是 `Pair<Employee>`，并且这样的赋值是不合法的。无论 S 与 T 有什么关系，通常，`Pair<S>` 与 `Pair<T>` 都没有任何关系。

## 通配符类型

严格的泛型系统使用起来并不那么让人愉快，类型系统的研究人员知道这一点已经有一段时间了。Java 的设计者发明了一种巧妙但是安全的解决方案：通配符类型。

### 通配符概念

在通配符类型中，允许类型参数发生变化，例如通配符类型 `Pair<? extends Employee>` 表示任何泛型 Pair 类型，它的类型参数是 Employee 的子类，如 `Pair<Manager>` 但不是 `Pair<String>`。假设要编写一个打印员工对的方法，如下所示：
```java
public static void printBuddies(Pair<Employee> p) {
    Employee first = p.getFirst();
    Employee second = p.getSecond();
    System.out.println(first.getName() + " and " + second.getName() + " are buddies.");
}
```
正如前面讲到的，不能将 `Pair<Manager>` 传递给这个方法，这一点很有限制。不同解决方法也很简单，可以使用一个通配符类型：`public static void printBuddies(Pair<? extends Employee> p)`。类型 `Pair<Manager>` 是 `Pair<? extends Employee>` 的子类型。

### 通配符的超类型限定

通配符限定与类型变量限定十分相似，但是还有一个附加的能力，即可以指定一个超类型限定(supertype bound)，如下所示：
```java
? super Manager
```
这个通配符限定为 Manager 的所有超类型。为什么要这样做呢？例如，`Pair<? super Manager>` 有如下方法：
```java
void setFirst(? super Manager) 
? super Manager getFirst()
```
下面是一个典型的实例。我们有一个经理数组，并且想把奖金最高和奖金最低的经理放在一个 Pair 对象中。Pair 的类型是什么？在这里，`Pair<Employee>` 是合理的，`Pair<Object>` 也是合理的，下面的方法将接受任何合适的 Pair：
```java
public static void minmaxBonus(Manager[] a, Pair<? super Manager> result) {
    if (a.length == 0) return;
    Manager min = a[0];
    Manager max = a[0];
    for (int i=1; i < a.length; ++i) {
        if (min.getBouns() > a[i].getBouns()) {
            min = a[i];
        }
        if (max.getBouns() < a[i].getBouns()) {
            max = a[i];
        }
    }
    result.setFirst(min);
    result.setSecond(max);
}
```
直观地讲，带有超类型限定的通配符允许你写入一个泛型对象，而带有子类型限定的通配符允许你读取一个泛型对象。

下面是超类型限定的另一种应用。Comparable 接口本身就是一个泛型类型。声明如下：
```java
public interface Comparable<T> {
    public int compareTo(T other);
}
```
在这里，类型变量指示了 other 参数的类型，例如，String 类实现了 `Comparable<String>`，它的 compareTo 方法声明为：`public int compareTo(String other)`，这很好，显式的参数有一个正确的类型。接口是泛型接口之前，other 是一个 Object，这个方法的实现必须有一个强制类型转换。

### 无限定通配符

还可以使用无限定的通配符，例如，`Pair<?>`。初看，这好像与原始的 Pair 类型一样。实际上，这两种类型有很大的不同。类型 `Pair<?>` 有以下方法：
```java
? getFirst()
void setFirst(?)
```
getFirst 的返回值只能赋给一个 Object，setFirst 方法不能被调用，甚至不能用 Object 调用。Pair<?> 和 Pair 本质区别在于：是否可用任意 Object 调用原始 Pair 类的 setFirst 方法。

为什么需要使用这样一个脆弱的类型？它对于很多简单操作非常有用。例如，下面这个方法可以用来测试一个对组是否包含一个 null 引用，它不需要实际的类型。
```java
public static boolean hasNulls(Pair<?> p) {
    return p.getFirst() == null || p.getSecond() == null;
}
```
通过将 hasNulls 转换成泛型方法，可以避免使用通配符类型：`public static <T> boolean hasNulls(Pair<T> p)`，但是带有通配符的版本可读性更好。

### 通配符捕获




