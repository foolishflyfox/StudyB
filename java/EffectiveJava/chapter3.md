# chapter 3 对于所有对象都通用的方法

尽管 Object 是一个具体类，但设计它主要是为了扩展。它所有的非 final 方法(equals、hashCode、toString、clone 和 finalize)都有明确的通用约定(general contract)，因为它们设计成是被覆盖的(override)。任何一个类在覆盖这些方法的时候，都有责任遵守这些通用的约定，如果不能做到这一点，其他依赖于这些约定的类(如 HashMap 和 HashSet)就无法结合该类一起正常运作了。

例如：
```java
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class T3 {
    static class MyString {
        private String str;
        public MyString(String str) {
            this.str = str;
        }
        @Override
        public int hashCode() {
            return str.length();
        }
        @Override
        public boolean equals(Object other) {
            return str.length() == ((MyString)other).str.length();
        }
    }
    public static void main(String[] args) {
        MyString s1 = new MyString("abc");
        MyString s2 = new MyString("app");
        Set<MyString> ss = new HashSet<>();
        ss.add(s1);
        ss.add(s2);
        System.out.println(ss);
    }
}
```
输出为：`[T3$MyString@3]`，在 ss 中只有一个元素。

## 第10条 覆盖 equal 时请遵守通用约定

覆盖 equals 方法看似很简单，但有许多覆盖方式会导致错误，并且后果非常严重。最容易避免这类问题的办法是不覆盖 equals 方法，在这种情况下，每个实例只与它自身相等。如果满足以下任何一个条件，这就是所期望的结果。

- 类的每个实例本质上都是唯一的。对于代表活动实例而不是值的类来说确实如此，例如 Thread。Object 提供的 equals 实现对于这些类来说正是正确的行为。
- 没有必要提供逻辑相等的测试功能。
- 超类已经覆盖了 equals，超类的行为对于这个类也是合适的。例如，大多数的 Set 实现都从 AbstractSet 继承 equals 实现，List 实现从 AbstractList 继承 equals 实现，Map 从 AbstractMap 继承 equals。
- 类是私有的，或者是包级私有的，可以确定它的 equeals 永远不会被调用。

“值类” 需要自己实现 equals 方法。值类仅仅是一个表示值的类，例如 Integer 或者 String。

在覆盖 equals 方法时，必须遵循它的约定。equals 方法实现了等价关系(equivalence relation)，其属性如下：
- 自反性(reflexive): 对于任何非 null 的引用值 x，x.equals(x) 必须返回 true。
- 对称性(symmetric): 对于如何非 null 的引用值 x 和 y，当且仅当 y.equals(x) 返回 true 时，x.equals(y) 返回 true。
- 传递性(transitive): 对于任何非 null 的引用值 x、y和z，如果 x.equals(y) = true，y.equals(z) 也为 true，则 x.equals(z) 必为 true。
- 一致性(consistent): 对于如何非 null 的引用值 x 和 y，只要 equals 的比较操作在对象所用的信息没有被修改，多次调用 x.equals(y) 就会一致地返回 true 或 false。
- 对于任何非 null 的引用值 x，x.equals(null) 必为 false。

## 第11条 覆盖 equals 时总要覆盖 hashCode

在每个覆盖了 equals 方法的类中，都必须覆盖 hashCode 方法。如果不这样做的话，会违反 hashCode 的通用约定，从而导致该类无法结合所有基于散列的集合一起正常工作。这类集合包括 HashMap、HashSet。下面是约定的内容：

- 在应用程序执行期间，只要对象的 equals 方法的比较操作所用到的信息没有被修改，那么对同一个对象的多次调用，hashCode 必须始终返回同一个值。一个应用程序与另一个应用程序的执行过程中，执行 hashCode 方法所返回的值可以不一致。
- 如果两个对象根据 equals(Object) 方法比较是相等的，那么调用这两个对象的 hashCode 方法都必须产生相同的整数结果。
- 如果两个对象根据 equals(Object) 方法比较是不相等的，那么调用这两个对象中的 hashCode 方法，则不一定要求 hashCode 方法必须产生不同的结果。但是，程序员应该知道，给不相等的对象产生截然不同的整数结果，有可能提高上列表(hash table)的性能。

HashMap 有一项优化，可以将与每个相关联的散列码缓存起来，如果散列码不匹配，也就不去检验对象的等同性。只有对象等同，才认为 key 相等。

编写一个合法但并不好用的 hashCode 方法没有任何价值。例如下面的方法总是合法，但是它永远不应该被正式使用：
```java
@Override
public int hashCode() { return 42; }
```
上面这个hashCode方法时合法的，因为它确保了相等的对象总是具有相同的散列码。但它也极为恶劣，因为它使得每个对象都具有相同的散列码。因此，每个对象都被映射到同一个散列桶中，使散列表退化为链表(Linked List)。它使得本该以 O(1) 时间获取元素的方法变为了 O(N) 的时间复杂度。

如果一个类是不可变的，并且计算散列码的开销比较大，就应该考虑将散列码缓存在对象内部，而不是每次请求都重新计算散列码。

## 第12条 始终覆盖 toString

虽然 Object 提供了 toString 方法的一个实现，但它返回的字符串通常不是类的用户所期望看到的。它包含类的名称，以及一个 `@` 符号，接着是散列码的无符号十六进制表示法。

toString 的通用约定指出，被返回字符串应该是一个简洁的但信息丰富，并且易于阅读的表达形式。toString 约定进一步指出，建议所有的子类都覆盖这个方法。

遵守 toString 的约定并不像遵守 equals 和 hashCode 的约定那么重要，但是，提供好的 toString 实现可以使类使用起来更加舒适，使用了这个类的系统也更加易于调试。

指定 toString 返回值的格式也有不足之处：如果这个类已经被广泛使用，一旦指定格式，就必须始终如一地坚持这种格式。程序员将会编写出相应的代码来解析这种字符串表示法、产生字符串表示法，以及把字符串表示法嵌入持久的数据中。如果将来的发行版中改变了这种表示法，就会破坏他们的代码和数据，他们当然会抱怨。如果不指定格式，就可以保留灵活性，便于在将来的发行版中增加信息。

无论是否指定格式，都为 toString 返回值中包含的所有信息提供一种可通过变成访问之的途径。如果不这样做，就会迫使需要这些信息的程序员不得不自己去解析这些字符串，除了降低了程序的性能，使得程序员去做这些不必要的工作之外，这个解析过程很容易出错，会导致系统不稳定，如果格式发生变化，还会导致系统崩溃。如果没有提供这些访问方法，即使你已经指明了字符串的格式会发生变化，这个字符串格式也成了事实上的 API。

在静态工具类和枚举类中编写 toString 方法。在所有其子类共享通用字符串的抽象类中，一定要编写一个 toString 方法。例如，大多数集合实现中的 toString 方法都继承自抽象的集合类。例如 ArrayList 的 toString 使用的是 AbstractCollection 的 toString 方法。

## 第13条 谨慎地覆盖 clone

Cloneable 接口的目的是作为对象的一个 mixin 接口(mixin interface)，标明这样的对象允许克隆(clone)。遗憾的是，它并没有成功地达到这个目的。它的主要缺陷在于缺少一个 clone 方法，而 Object 的 clone 是受保护的。如果不借助反射，就不能仅仅因为一个对象实现了 Cloneable 旧调用 clone 方法。即使是 反射调用也可能失败，因为不能保证该对象一定具有可访问的 clone 方法。

既然 Cloneable 接口没有包含任何方法，那么它到底有什么作用？它决定了 Object 中受保护的 clone 方法实现的行为：如果一个类实现了 Cloneable，Object 的 clone 发就返回该对象的逐域拷贝，否则就抛出 CloneNotSupportedException 异常。这是接口的一种极端非典型用法，不值得效仿。通常情况下，实现接口是为了表明类可以为它的客户做些什么。然而对于 Cloneable 接口，它改变了超类中受保护的方法的行为。

虽然规范中没有明确指出，事实上，实现 Cloneable 接口的类是为了提供一个功能适当的公有的 clone 方法。为了达到这个目的，类及其所有超类必须都遵守一个相当复杂的、不可实施的，并且基本上没有文档说明的协议。由此得到一种语言之外的机制：它无需调用构造器就可以创建对象。

一般来说，对于任何对象 x，表达式 `x.clone() != x` 将会返回结果 true，表达式 `x.clone().getClass()==x.getClass()`返回 true。虽然通常情况下，表达式 `x.clone.equals(x)`将会返回 true，但这不是绝对的要求。

子类只能调用受保护的 clone 方法来克隆它自己。必须重新定义 clone 为 public 才能允许所有方法克隆对象。

在这里，Cloneable 接口的出现与接口的正常使用没有关系。具体来说，它没有指定 clone 方法，这个方法是从 Object 继承而来的。这个接口只是作为一个标记，指示类设计者了解克隆过程。对象对于克隆很“偏执”，如果一个对象请求克隆，但是没有实现这个接口，就会生成一个检查型异常。

Cloneable 接口是 Java 提供的少数标记接口(tagging interface)之一。标记接口不包含任何方法，唯一作用是允许在类型查询中使用 instanceof： `if(obj instanceof Cloneable)`。建议你自己的程序中不要使用标记接口。

例如：
```java
public class T7 {
    public static void main(String[] args) throws CloneNotSupportedException {
        Person p1 = new Person("abc", new AtomicInteger(0));
        Person p2 = p1.clone();
        p2.age.getAndIncrement();
        System.out.println(p1.age);
    }

    @AllArgsConstructor
    static class Person implements Cloneable{
        String name;
        AtomicInteger age;
        @Override
        public Person clone() throws CloneNotSupportedException {
            return (Person) super.clone();
        }
    }
}
```
如果将 `implements Cloneable` 删除，将会报 CloneNotSupportedException 异常。

在 Java 1.4 之前，clone 方法的返回类型总是 Object，而现在可以为你的 clone 方法指定正确的返回类型，这是协变返回类型的一个例子。协变返回意味着，当一个方法重写
时，允许重写的返回类型为原返回类型的子类型。

## 第14条 考虑实现 Comparable

comparableTo 方法没有在 Object 中声明，它是 Comparable 接口中唯一的方法。compareTo 方法不但允许进行简单的等同行比较，而且允许执行顺序比较，除此之外，它与 Object 的 equals 方法具有类似的特征，它还是个泛型(generic)。类实现了 Comparable 接口，就表名它的实例具有内在的排序关系(natural ordering)。为了实现 Comparable 接口的对象数组进行排序，可以通过 `Arrays.sort(a)` 即可。

一旦类实现了 Comparable 接口，它就可以跟许多泛型算法以及依赖于该接口的集合实现进行协作。你付出很小的努力就可以获得非常强大的功能

事实上，Java 平台类库中的所有值类(value classes)，以及所有的枚举类型都实现了 Comparable 接口。如果你正在编写一个值类，它具有非常明显的内在排序关系，比如按字母顺序、按数值顺序或按年代顺序，那你就应该坚决考虑实现 Comparable 接口：
```java
interface Comparable<T> {
    boolean compareTo(T t);
}
```
compareTo 方法的通用约定与 equals 方法的约定相似：
将这个对象有指定的对象进行比较。当该对象小于、等于或大于指定对象的时候，分别返回一个负整数、零或者正整数。如果由于指定对象的类型而无法与该对象进行比较，则抛出 ClassCastException 异常。

实现者必须确保所有的 x 和 y 都满足 sgn(x.compareTo(y)) == -sgn(y.compareTo(x))。还应该保证这个比较关系是可传递的。

