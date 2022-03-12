## chapter9 不断演进的 API

同时定义接口以及辅助类是 Java 语言常用的一种模式，工具类定义了与接口实例协作的很多静态方法。例如，Collections 就是处理 Collection 对象的辅助类。由于静态方法可以存在于接口内部，你代码中的这些辅助类就没有存在的必要了。你可以将这些静态方法移到接口内部。

## 用 optional 取代 null

Optional 使用的优点：它通过类型系统让你的域模型中隐藏的知识显式地体现在代码中。换句话说，你永远都不应该忘记语言的首要功能就是沟通，即使对程序设计语言而言也没有什么不同。声明方法接收一个 Optional 参数，或者将结果作为 Optional 类型返回，让你的同事或者未来你方法的使用者很清楚地知道它可以接收空值，或者它可能返回一个空值。

Optional 的 map 和 flatMap 的差异：
```java
class Optional<T> {
    public<U> Optional<U> map(Function<? super T, ? extends U> mapper) {
        Objects.requireNonNull(mapper);
        if (!isPresent())
            return empty();
        else {
            // 返回类型为类型为 Optional<U>
            return Optional.ofNullable(mapper.apply(value));
        }
    }

        public<U> Optional<U> flatMap(Function<? super T, Optional<U>> mapper) {
        Objects.requireNonNull(mapper);
        if (!isPresent())
            return empty();
        else {
            // 返回类型 U
            return Objects.requireNonNull(mapper.apply(value));
        }
    }
    ... ...
}
```
总结：如果返回的也是 Optional 则使用 flatMap，否则使用 map 即可。

由于 Optional 类设计时就没有特别考虑将其作为类的字段使用，所以它也并未实现 Serializable 接口。由于这个原因，如果你的应用使用了某些要求序列化的库或者框架，在域模型中使用 Optional 有可能引发应用程序故障。

如果你一定要上线系列化的域模型，作为替代方案，可以提供一个能访问声明为 Optional、变量值可能缺失的接口：
```java
public class Person {
    private Car car;
    public Optional<Car> getCarAsOptional () {
        return Optional.ofNullable(car);
    }
}
```

### 默认行为及解引用 Optional 对象

Optional 类提供了多种方法读取 Optional 实例中的变量值。
- `get()`: 最简单但又是最不安全的方法。如果变量存在，它直接返回封装的变量，否则抛出一个 `NoSuchElementException` 异常。所以，除非你非常确定 Optional 变量一定包含值，否则使用这个方法是个相当糟糕的注意。此外，这种方式即便相对于嵌套式的 null 检查也并未体现出多大的改进。
- `orElse(T other)`: 允许在 Optional 对象中不包含值时提供一个默认值。
- `orElseGet(Supplier<? extends T> other)`: 是 orElse 方法延迟调用版，Supplier 方法只有在 Optional 对象不含有值时才执行调用。如果创建默认值是件耗时耗力的工作，你应该考虑采用这种方式(借此提升程序的性能)，或者你需要非常确定某个方法仅在 Optional 为空时才调用，也可以考虑该方法(这种情况有严格的限制条件)。
- `orElseThrow(Supplier<? extends X> exceptionSupplier)`: 和 get 方法非常类似，它们遭遇 Optional 对象为空时，都会抛出一个异常，但使用 orElseThrow 你可以定制希望抛出的异常类型。
- `ifPresent(Consumer<? super T>)`: 让你能在变量存在时执行一个作为参数传入的方法，否则就不进行任何操作。

Optional 类和 Stream 接口的相似之处，远不止 map 和 flatMap 这两个方法，还有第三个方法 filter。

### 两个 Optional 对象组合

现在，我们假设你有这样一个方法，它接收一个 Person 和一个 Car 对象，并以此为条件对外部提供的服务进行查询，通过一些复杂的业务逻辑，试图找到满足该组合的最便宜的保险公司。
```java
public Insurance findCheapestInsurance(Person person, Car car) {
    // 不同的保险公司提供的查询服务
    // 对比所有数据
    return cheapestCompany;
}
```
我们假设你还想要该方法的一个 null-安全的版本，它接受两个 Optional 对象作为参数，返回值是一个 `Optional<Insurance>` 对象，如果传入的任何一个参数为空，它返回值亦为空。

初始版本：
```java
public Optional<Insurance> findCheapestInsurance(
    Optional<Person> person, Optional<Car> car) {
    if (!person.isPresent() || !car.isPresent()) {
        return Optional.empty();
    }
    return findCheapestInsurance(person.get(), car.get());
}
```
这个方法具有明显的优势，我们从它的签名中就能非常清楚地知道无论是 person 还是 car，它的值都有可能为空，出现这种情况时，方法的返回值也不会包含任何值。

不幸的是，该方法的具体实现和你之前的null检查非常相似。另一种实现：
```java
public Optional<Insurance> findCheapestInsurance(
    Optional<Person> person, Optional<Car> car) {
    return person.flatMap(p -> car.map(c -> findCheapestInsurance(p, c)));
}
```
### 使用 filter 剔除特定的值

你经常需要调用某个对象的方法，查看它的某些属性。为了以一种安全的方式进行这些操作，你首先需要确定引用指向的 Insurance 对象是否为 null，之后再调用它的 getName 方法，如下所示：
```java
Insurance insurance = ...;
if(insurance !=null && "CambridgeInsurance".equals(insurance.getName())) {
    System.out.println("ok");
}
```
使用 Optional 对象的 filter 方法，这段代码可以重构如下：
```java
Optional<Insurance> optInsurance = ...;
optInsurance.filter(insurance -> "CambridgeInsurance".equals(insurance))
    .ifPrecent(x -> System.out.println("ok"));
```
filter 方法接受一个谓词作为参数。如果 Optional 对象的值存在，并且它符合谓词的条件，filter 方法就返回其值；否则他就返回一个空的 Optional 对象。

下面对 Optional 类的方法进行了分类和概括：

|方法|描述|
|---|---|
|empty|返回一个空的 Optional 实例|
|filter|如果值存在，并且满足提供的谓词，就返回包含该值的Optional对象，否则空的 Optional 对象|
|flatMap|如果值存在，就对该值执行提供的 mapping 函数调用，返回一个 Optional 类型，否则返回一个空的 Optional对象|
|get|获取值，空则抛出异常|
|ifPresent|存在，之后函数调用，否则啥都不做|
|isPresent|判断是否存在|
|map|存在的话，就该值执行提供的 mapping 函数调用|
|of|将指定值用 Optional 封装后返回，如果该值为 null，则抛出NPE|
|ofNullable|指定值用 Optional 封装后返回，如果为null，则返回一个空的Optional对象|
|orElse|如果有值，则将其返回，否则返回一个默认值|
|orElseGet|如果有值，则将其返回，否则调用一个由指定的 Supplier 接口生成的值(延迟执行)|
|orElseThrow|如果有值，将其返回，否则抛出一个自定义错误|

## 使用 Optional 的实战示例

### 用 Optional 封装可能为 null 的值

现存的 JavaAPI 几乎都通过返回一个 null 的方式来表示需要值的缺失，或者由于某些原因计算无法得到该值。比如，如果 Map 中不含指定的键对应的值，它的 get 方法会返回一个 null。但是，正如我们之前介绍的，大多数情况下，你可能希望这些方法能返回一个 Optional 对象。你无法修改这些方法的签名，但是你很容易用 Optional 对这些方法的返回值进行封装。我们接着用 Map 做例子，假设你有一个 `Map<String, Object>` 方法，访问由 key 索引的值时，如果 map 没有与 key 关联的值，该次调用就返回一个 null：
```java
Object value = map.get("key");
```
使用 Optional 封装 map 的返回值，你可以对这段代码进行优化。要达到这个目的有两种方式：你可以用笨拙的 if-then-else 判断语句，毫无疑问，这种方式会增加代码的复杂度；或者你可以采用我们前文介绍的 `Optional.ofNullable` 方法：
```java
Optional<Object> value = Optional.ofNullable(map.get("key"));
```

### 异常与 Optional 的对比

由于某种原因，函数无法返回某个值，这时除了返回 null，Java API 比较常用的替代做法是抛出一个异常。这种情况比较典型的例子使用静态方法 Integer.parseInt(String)，将 String 转换为 int。在这个例子中，如果 String 无法解析到对应的整型，就抛出 NumberFormatException。最后的效果是，发生 String 无法转换为 int 时，代码发出一个遭遇非法参数的信号，唯一的不同是，这次你需要使用 try/catch 语句，而不是使用 if 语句判断来控制一个变量的值是否非空。

你也可以用空的 Optional 对象，对遭遇无法转换的 String 时返回的非法值进行建模，这时你期望 parseInt 的返回值是一个 optional。我们无法修改最初的 Java 非法，但是这不妨碍我们进行需要的改造，你可以实现一个工具方法，将这部分逻辑封装于其中，最终返回一个我们希望的 Optional 对象，代码如下所示：
```java
public static Optional<Integer> stringToInt(String s) {
    try {
        return Optional.of(Integer.parseInt(s));
    } catch (NumberFormatException e) {
        return Optional.empty();
    }
}
```
我们的建议是，你可以将多个类似的方法封装在一个工具类中，让我们称之为 OptionalUtility。通过这种方式，你以后就能注解调用 OptionalUtility.stringToInt 方法，将 String 转换为一个 `Optional<Integer>` 对象，而不需要记得你在其中封装了笨拙的 try/catch 逻辑。

**基础类型的 Optional 对象，以及为什么应该避免使用它们？**


与 Stream 一样，Optional 也提供了类似的基础类型：OptionalInt、OptionalLong 以及 OptionalDouble。如果 Stream 对象包含大量元素，出于性能考虑，使用基础类型是不错的选择，但对于 Optional 而言，这个理由就不成立了，因为 Optional 对象最多只包含一个值。

我们不推荐大家使用基础类型的 Optional，因为基础类型的 Optional 不支持 map、flatMap以及filter方法，而这些是 Optional 类最有用的方法。

