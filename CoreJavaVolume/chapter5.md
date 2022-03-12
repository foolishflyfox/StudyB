# 继承

## Object: 所有类的超类

Object 是 Java 中所有类的超类，在 Java 中每个类都扩展了 Object。如果没有明确指出超类，Object 就被认为是这个类的超类。

在 Java 中，只有基本类型(primitive type)不是对象，例如数值、字符和布尔类型。所有的数组类型，不管是对象数组还是基本类型的数组，都扩展自Object。

### equals 方法

Object 类中的 equals 方法用于检测一个对象是否等于另一个对象。Object 类中实现的 equals 方法将确定两个对象的引用是否相等。对于很多类而言，这已经足够了。不过，经常需要基于状态检测对象的相等性，如果两个对象有相同的状态，才认为两个对象是相等的。例如：
```java
public class Employee {
    public String name;
    public int salary;
    public Employee(String name, int salary) {
        this.name = name;
        this.salary = salary;
    }
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (null == other) {
            return false;
        }
        if (getClass()!=other.getClass()) {
            return false;
        }
        Employee otherEmployee = (Employee) other;
        return otherEmployee.salary == salary &&
                (Objects.equals(name, otherEmployee.name));
    }
}
```
在子类中定义 equals 方法时，首先调用超类中的 equals。如果检测失败，对象就不可能相等。如果超类中的字段都相等，就需要比较子类中的实例字段。
```java
public class Manager extends Employee {
    ...
    @Override
    public boolean equals(Object otherObject) {
        if (!super(this, otherObject)) {
            return false;
        }
        Manager other = (Manager)otherObject;
        return bonus == other.bonus;
    }
}
```

### 相等测试与继承

如果隐式和显式的参数不属于同一个类，equals 方法将如何处理呢？这是一个很有争议的问题。在前面的例子中，如果发现类不匹配，equals 方法就返回 false。但是，许多程序员却喜欢使用 instanceof 进行检测：
```java
if(!(other instanceof Employee)) {
    return false;
}
```
这样就允许 other 属于一个子类，可能会导致下面的对称性不能满足。Java 语言规范要求 equals 方法具有下面的特性：
- 自反性: 对于任何非空引用 x，x.equals(x) 应该返回 true；
- 对称性：对于任何引用，当且仅当 y.equals(x) 返回 true 时，x.equals(y) 返回 true；
- 传递性：对于如何引用，如果 x.equals(y) 返回 true，y.equals(z) 返回 true，则 x.equals(z) 返回 true；
- 一致性：如果 x、y 引用没有发送变化，反复调用 x.equals(y)应该返回同样的结果；
- 对于任意非空引用x，x.equals(null)应该返回false；

有两种完全不同的情形：
- 如果子类可以有自己的相等性概念，则对称性需求将强制使用 getClass 检测；
- 如果有超类决定相等性概念，则可以用 instanceof 检测，这样可以在不同子类的对象之间进行相等性比较；

### hashCode 方法

散列码 (hash code) 是由对象导出的一个整数值，散列码是没有规律的，如果 x 和 y 是两个不同的对象，x.hashCode() 和 y.hashCode() 基本上不会相同。

下面是 String hashCode 的计算：
```java
public class T1 {
    public static void main(String[] args) {
        System.out.println("Hello".hashCode());
        System.out.println(calcStringHashCode("Hello"));
    }
    static int calcStringHashCode(String s) {
        int hash = 0;
        for (int i=0; i < s.length(); ++i) {
            hash = hash * 31 + s.charAt(i);
        }
        return hash;
    }
}
```
如果重新定义了 equals 方法，就必须为用户可能插入散列表的对象重新定义 hashCode 方法。hashCode 方法应该返回一个整数，也可以是负数。要合理地组合实例字段的散列码，以便能够让不同对象产生的散列码分布更加均匀。

例如，下面是 Employee 类的 hashCode 方法：
```java
@Override
public int hashCode() {
    return Objects.hash(name, salary);
}
```
equals 必须与 hashCode 的定义相容：如果 x.equals(y) 返回 true，那么 x.hashCode() 就必须与 y.hashCode() 返回相同的值。

### toString 方法

在 Object 中还有一个重要方法 toString，它会返回表示对象值的一个字符串。绝大多数的 toString 方法都遵循这样的格式：类的名字，随后是一对方括号括起来的字段值。

例如：
```java
public class T6 {
    public static void main(String[] args) {
        Point point = new Point(12, 32);
        System.out.println(point);
        Circle circle = new Circle(5, 3, 1);
        System.out.println(circle);
    }
}
```
输出为：
```
java.awt.Point[x=12,y=32]
Circle[centerX=5.0, centerY=3.0, radius=1.0, fill=0x000000ff]
```
参考 Point 的 toString 方法：
```java
public String toString() {
    return getClass().getName() + "[x=" + x + ",y=" + y + "]";
}
```


