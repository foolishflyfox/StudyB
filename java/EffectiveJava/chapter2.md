# Effective Java

## 第二章 创建和销毁对象

### 1. 用静态工厂方法代替构造器

例子：
```java
    public static Boolean valueOf(boolean b) {
        return (b ? TRUE : FALSE);
    }
```

静态工厂方法与构造器相比的优势：

1. 静态方法可以自定义函数名，更利于代码阅读；
2. 相同类型的参数可以根据需求的不同返回不同的创建对象，因此当一个类需要多个带有相同签名的构造器时，就用静态工厂方法替代构造器；
3. 不必在每次调用它们的时候都创建一个新对象，，如果程序经常请求创建相同的对象，并且创建对象的代价很高，这项技术可以极大提高性能；
4. 可以返回原返回类型的任何子类型对象，一种应用是：API 可以返回对象，同时又不会使对象的类变成公有的；
5. 所返回的对象的类可以随着每次调用而发生变化，只要是已声明类型的子类都是允许的，因此可以保持接口不变，升级具体实现；
6. 方法返回的对象所属的类，在编写包含该静态工厂方法的类时，可以不存在。这种灵活的静态工厂方法构成了服务提供者框架的基础；


静态工厂方法的缺点：

1. 类如果不含有共有的或受保护的构造器，就不能被子类化。但也可以因祸得福，因为Java鼓励程序员使用复合(composition)而非继承，这正是不可变类型所需要的；
2. 程序员很难发现它们，它们没有像构造器那样在 API 文档中明确标识出来；

静态方法的一些惯例：
- `from`，类型转换方法，它只有单个参数，返回该类型的一个对应的实例，例如 `Data d = Data.from(instance)`；
- `of`，聚合方法，带有多个参数，返回该类型的一个实例，并把它们合并起来，例如 `Set<Rank> faceCard = EnumSet.of(JACK, QUEEN, KING)`；
- `valueOf`，比 from 和 of 更繁琐的一种替代方法，例如 `BigInteger prime = BigInteger.valueOf(Integer.MAX_VALUE)`；
- `instance` 或 `getInstance`，返回的实例是通过方法的参数来描述的；
- `create` 或 `newInstance`，与 `instance` 或 `getInstance` 类似，但保证每次调用都返回一个新实例；
- `getType`，像 getInstance 一样，但是在工厂方法处于不同的类中的时候，使用 Type 表示工厂方法所返回的对象类型；
- `newType`，像 newInstance 一样，但是在工厂方法处于不同的类中的时候，使用 Type 表示工厂方法所返回的对象类型；
- `type`，是 `getType` 和 `newType` 的简化版；

### 2. 遇到多个构造器参数时要考虑使用构建器

静态工厂和构造器有个共同的局限：不能很好地扩展到大量地可选参数。对于有许多可选参数的构造器，程序员一向习惯采用**重叠构造器(telescoping constructor)**模式。在这种模式下，提供的第一个构造器只有必要的参数，第二个构造器有一个可选参数，第三个构造器有两个可选参数，以此类推。重叠构造器模式可行，但如果有许多参数的时候，客户端代码会很难编写，并且难以阅读。

也可以使用 JavaBeans 模式，先调用一个无参构造器创建对象，然后调用 Setter 方法来设置每个必要的参数。缺点是，因为构造过程被分到几个调用中，在构造过程中 JavaBean 可能处于不一致的状态。另外，JavaBeans 模式使得把类做成不可变的可能性不复存在。这就需要程序员付出额外的努力来确保它的线程安全。

建造者模式可以让客户端利用所有必要的参数调用构造器（或静态工厂），然后得到一个 builder 对象。然后客户端在 builder 对象调用类似 setter 的方法来设置每个相关的可选参数。最后客户端调用无参的 build 方法来生成通常是不可变的对象。这个 builder 通常是它所构建的类的静态成员类。例如：
```java
// Builder Pattern
public class NutritionFacts {
    private final int servingSize;
    private final int servings;
    private final int calories;
    private final int fat;
    private final int sodium;
    private final int carbohydrate;

    public static class Builder {
        // Required parameters
        private final int servingSize;
        private final int servings;

        // Optional parameters - initialized to default values
        private int calories = 0;
        private int fat = 0;
        private int sodium = 0;
        private int carbohydrate = 0;

        public Builder(int servings, int servingSize) {
            this.servings = servings;
            this.servingSize = servingSize;
        }

        public Builder calories(int val) { this.calories = val; return this; }
        public Builder fat(int val) { this.fat = fat; return this; }
        public Builder sodium(int val) { this.sodium = val; return this; }
        public Builder carbohydrate(int val) { this.carbohydrate = val; return this; }

        public NutritionFacts build() { return new NutritionFacts(this); }
    }

    private NutritionFacts(Builder builder) {
        this.servingSize = builder.servingSize;
        this.servings = builder.servings;
        this.calories = builder.calories;
        this.fat = builder.fat;
        this.sodium = builder.sodium;
        this.carbohydrate = builder.carbohydrate;
    }
}
```
builder 的设置方法返回 builder 本身，以便把调用链接起来，得到一个流式 API。例如 `NutritionFacts cocaCala = new NutritionFacts.Builder(240, 8).calories(100).sodium(35).carbohydrate(27).build();`。

Builder 模式模拟了具名的可选参数。

如果类的构造器或者静态工厂中具有多个参数，设计这种类时，Builder 模式就是一种不错的选择。

### 3. 用私有构造器或枚举类型强化 Singleton 属性

Singleton 通常被用来代表一个无状态的对象，如函数，或者那些本质上唯一的系统组件。实现 Singleton 有两种常用方法，这两种方法都要求保持构造器为私有，并导出公有的静态成员，以便允许客户端能够访问该类的唯一实例。

第一种方法：
```java
// Singleton with public final field
public class Elvis {
    public static final Elvis INSTANCE = new Elvis();
    private Elvis() { ... }

    public void levelTheBuilding() { ... }
}
```
私有构造器仅被调用一次，用于实例化公有的静态 final 域 Elvis.INSTANCE 。由于缺少public或protected的构造器，所以保证了Elvis实例的唯一性。需要提醒的一点：享有特权的客户端可以借助 AccessibleObject.setAccessible 方法，通过反射机制调用构造器。如果要抵御这种攻击，可以修改构造器，让它在被要求创建第二个实例的时候抛出异常。

第二种方法：
```java
// Singleton with factory
public class Elvis {
    private static final Elvis INSTANCE = new Elvis();
    private Elvis() { ... }
    public static Elvis getInstance() { return INSTANCE; }

    public void levelTheBuilding() { ... }
}
```
公有域方法的主要优势在于，API 很清楚地标明了这个类是一个 Singleton。
静态工厂方法的优势是，它提供了灵活性。在不改变其 API 的前提下，我们可以改变该类是否应该为 Singleton 的想法。为了利用上述方法实现的 Singleton 类变成可序列化，仅在声明上加上 implements Serializable 是不够的，为了维护并保证 Singleton，必须声明所有实例域都是瞬时(transient)的，并提供一个 readResolve 方法，否则每次反序列化一个序列化的实例时，都会重建一个新的实例。

实现 Singleton 的第三种方法是声明一个包含单个元素的枚举类型：
```java
public enum Elvis {
    INSTANCE;

    public void leaveTheBuilding() { ... }
}
```
这种方法在功能上与公有域方法类似，但更简洁，无偿提供了序列化机制，绝对防止多次序列化，即使是在面对复杂的序列化或反射攻击的时候。虽然这种方法没有被广泛采用，但单元素的枚举类型经常成为实现 Singleton 的最佳方法。如果 Singleton 必须扩展一个超类，而不是扩展 Enum 的时候，则不宜使用该方法。


### 4. 通过私有构造器强化不可实例化的能力

有时候可能需要编写只包含静态方法和静态域的类。这样的工具类(utility class)不希望被实例化，然而在缺少显式构造器的情况下，编译器会自动提供一个公有的无参缺省构造器(default constructor)。

企图通过将类做成抽象类来强制该类不可被实例化是行不通的，该类可以被子类化，并且该子类也可以被实例化。通过包含一个私有构造器，可以实现一个类不被实例化：
```java
// Noninstantiable utility class
public class UtilityClass {
    // Suppress default constructor for noninstantiability
    private UtilityClass () { throw new AssertionError(); }
}
```
AssertionError 不是必需的，但是它可以避免不小心在类的内部调用构造器，保证该类在任何情况下都不会被实例化。

这种习惯用法也有副作用，它使得一个类不能被子类化，因为所有的构造器都必须显式或隐式地调用超类(superclass)的构造器。子类构造器对super方法的调用必须是其构造器中的第一个语句。

### 5. 优先考虑依赖注入来引入资源

有许多类会依赖一个或多个底层的资源。例如，拼写检查器需要依赖词典。因此，像下面这样把类实现为静态工具类的做法并不少见。
```java
public class SpellChecker {
    private static final Lexicon dictionary = ... ;
    private SpellChecker() {}  // 不可实例化
    public static boolean isValidate(String word) { ... }
    public static List<String> suggestions(String typo) { ... }
}
```
同样地，将这些类实现为单例的做法也不少见：
```java
public class SpellChecker {
    private final Lexicon dictionary = ... ;
    private SpellChecker(...) {}
    public static INSTANCE = new SpellChecker(...);

    public boolean isValidate(String word) { ... }
    public List<String> suggestiongs(String typo) { ... }
}
```
静态工具类和单例类不适合需要应用底层资源的类。依赖注入(dependency injection)用于指定底层资源类：
```java
public class SpellChecker {
    private final Lexicon dictionary;
    public SpellChecker(Lexicon dictionary) {
        this.dictionary = dictionary;
    }
    public boolean isValidate(String word) { ... }
    public List<String> sugguestions(String typo) { ... }
}
```
这种程序模式的另一种有用的变体是，将资源工厂(factory)传给构造器。工厂是可以被重复调用来创建类型实力的一个对象。这类工厂具体表现为工厂方法(Factory Method)模式。

在 Java 8 中添加的接口 `Supplier<T>` 最适合用于表示工厂。带有 `Supplier<T>` 的方法，通常应该限制输入工厂的类型参数使用有限制的通配符类型，以便客户端能够传入一个工厂，来创建指定类型的任意类型以及子类型。

虽然依赖注入极大地提升了灵活度和可测试性，但它会导致大型项目凌乱不堪，因为它通常包含上千个依赖，不过这种凌乱用一个依赖注入框架(dependency injection framework)就可以终结。

总而言之，不要用 Singleton 和静态工具来实现依赖一个或多个底层资源的类，且该资源的行为会影响到该类的行为；也不要直接用这个类来创建这些资源，而应该将这些资源或者工厂传给构造器(或者静态工厂，或者构建器)，通过他们来创建累。这个时间被称为依赖注入，它极大地提升了类的灵活性、可重用性和可测试性。

### 6. 避免创建不必要的对象

一般来说，最好采用单个对象，而不是每次需要的时候就创建一个相同功能的新对象。如果对象是不可变的(immutable)，它就始终能被重用。

例如 `String s = new String("bikini");` ，该语句每次被执行的时候都创建一个新的 String 实例，但是这些创建对象的动作全都是不必要的。传递给 String构造器的参数 `"bikini"` 本身就是一个 String 实例，功能等同于构造器创建的所有对象。改进后的版本为 `String s = "bikini"`。

对于同时提供了静态工厂方法(Static Factory Method)和构造器的不可变类，优先使用静态工厂而非构造器，以避免创建不必要的对象。例如，静态工厂 `Boolean.valueOf(String)` 几乎总是优先于构造器 `Boolean(String)`，该构造器在 Java 9 中已经被废弃。

另外，优先使用基本类型而不是装箱类型，当心无意识的自动装箱。

注意，如果不是非常重量级的对象，不建议使用对象池实现对象的重用，因为这样会导致代码非常复杂。另外，JVM 具有高度优化的垃圾回收器，使用对象池反而可能使性能降低。

### 7. 消除过期的对象引用

所谓的过期引用，是指永远不会被解除的引用。如果一个对象呗无意识地保留起来，那么垃圾回收机制不仅不会处理这个对象，也不会处理这个对象所引用的所有其他对象。

这类问题的修复方法很简单，一旦对象引用已经过期，只需要清空这些引用即可。消除过期引用最好的方法时让包含该应用的变量结束其生命周期，如果你是在最紧凑的作用域范围内定义每一个变量，这种情形就会自然而然发生。

一般来说，只要类是自己管理内存，程序员就应该警惕内存泄漏问题。一旦元素被释放掉，则该元素中包含的任何对象引用都应该被清空。

内存泄漏的另一个创建来源是缓存。一旦你把对象引用放到缓存中，它就很容易地被遗忘掉，从而使得它不再有用之后很长时间内仍然在缓存中。

如果你正好要上线这样的缓存：主要在缓存之外存在对某个项的**键**的引用，该项就有意义，那么久可以用 WeakHashMap 代表缓存。记住：只有在当所要的缓存项的生命周期由该键的外部引用而不是由值决定时，WeakHashMap 才有用处。

### 9. try-with-resources 优于 try-finally

Java 类库中包括许多必须通过调用 close 方法来手工关闭的资源。例如 InputStream、OutputStream 和 java.sql.Connection。客户端经常会忽略资源的关闭，造成严重的性能后果也就可想而知了。虽然这其中的许多资源都是使用终结方法作为安全网，但是效果不理想。

根据经验，try-finally 语句是确保资源会被适时关闭的最佳方法，就算发生异常或者返回也一样。
```java
// try-finally - No longer the best way to close resources
static String firstLineOfFile(String path) throws IOException {
    BufferReader br = new BufferReader(new FileReader(path));
    try {
        return br.readLine();
    } finally {
        br.close();
    }
}
```
这看起来也不算太坏，但如果再添加第二个资源，就会一团糟。
```java
// try-finally is ugly when used with more than one resource!
static void copy(String src, String dst) throws IOException {
    InputStream in = new FileInputStream(src);
    try {
        OutputStream out = new FileOutputStream(dst);
        try {
            byte[] buf = new byte[BUFFER_SIZE];
            int n;
            while ((n=in.read(buf)) >= 0) {
                out.write(buf, 0, n);
            } 
        } finally {
            out.close();
        }
    } finally {
        in.close();
    }
}
```
在 2007 年，close 方法在 java 类库中有 2/3 都用错了。

即便 try-finally 正确关闭了资源，它也存在不足。因为 try 和 finally 中的代码，都会抛出异常。例如，在 firstLineOfFile 方法中，如果底层的物理设备异常，那么调用 readLine 就会抛出异常，基于同样的原理，调用 close 也会出现异常，那么调用 close 也会出现异常。在这种情况下，第二个异常完全抹除了第一个异常。在异常堆栈轨迹中，完全没有关于第一个异常的记录，这在现实的系统中会导致调试变得非常复杂，因为通常需要看第一个异常才能诊断出问题所在。虽然可以通过编写代码来禁止第二个异常，保留第一个异常，但事实上没人这么做，因为实现太麻烦了。

例如：
```java
import java.io.IOException;

public class T1 {
    public static void main(String[] args) {
        Tool tool = new Tool(15);
        try {
            tool.use();
            System.out.println("after use");
        } catch (IOException e) {
            tool.close();
            System.out.println("after close");
        }
        System.out.println("end of main");
    }

    static class Tool {
        int v;
        public Tool(int v) { this.v = v; }
        public void use() throws IOException{
            if (v % 5 == 0) {
                throw new IOException("exception in use()");
            }
            System.out.println("use success");
        }
        public void close() {
            if (v % 3 == 0) {
                throw new RuntimeException("exception in close");
            }
            System.out.println("close success");
        }
    }
}
```
执行结果为：
```
$ javac T1.java
$ java T1
Exception in thread "main" java.lang.RuntimeException: exception in close
        at T1$Tool.close(T1.java:27)
        at T1.main(T1.java:10)
```
可以看到，IOException 被覆盖了。

当 Java 7 引入了 try-with-resources 语句时，这些问题都解决了。要使用这个构造的资源，必须先实现 AutoCloseable 接口，其中包含了单个返回 void 的 close 方法。Java 类库与第三方类库中的许多类和接口现在都实现或扩展了 AutoCloseable 接口。如果能编写一个类，它代表的是必须被关闭的资源，那么这个类也应该实现 AutoCloseable。

以下是使用 try-with-resources 的第一个范例：
```java
static Strig firstLienOfFile(String path) throws IOException {
    try (BufferedReader br = new BufferedReader(new FileReader(path))) {
        return br.readLinne();
    }
}
```
以下是使用 try-with-resources 的第二个范例：
```java
static void copy(String src, String dst) throws IOException {
    try (InputStream in = new FileInputStream(src)) {
        try (OutputStream out = new FileOutputStream(dst)) {
            byte[] buf = new byte[BUFFER_SIZE];
            int n;
            while ((n=in.read(buf)) >= 0) {
                out.write(buf, 0, n);
            } 
        } 
    } 
}
```
例如：
```java
import java.io.IOException;

public class T1 {
    public static void main(String[] args) throws IOException {
        try (Tool tool = new Tool(15)) {
            tool.use();
            System.out.println("after use");
        } 
        System.out.println("end of main");
    }

    static class Tool implements AutoCloseable {
        int v;
        public Tool(int v) { this.v = v; }
        public void use() throws IOException{
            if (v % 5 == 0) {
                throw new IOException("exception in use()");
            }
            System.out.println("use success");
        }
        @Override
        public void close() {
            System.out.println("enter close");
            if (v % 3 == 0) {
                throw new RuntimeException("exception in close");
            }
            System.out.println("close success");
        }
    }
}
```
结果为：
```java
enter close
Exception in thread "main" java.io.IOException: exception in use()
        at T1$Tool.use(T1.java:17)
        at T1.main(T1.java:6)
        Suppressed: java.lang.RuntimeException: exception in close
                at T1$Tool.close(T1.java:25)
                at T1.main(T1.java:8)
```
并没有掩盖原始抛错位置。

在打开资源时如果发生异常(执行 try 圆括号中的代码时)，那么 close 不会被执行，使用 catch 可以捕获相应的异常。


