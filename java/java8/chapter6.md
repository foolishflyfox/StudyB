# 用流收集数据

你可以把Java8的流看作花哨又懒惰的数据迭代器。它们支持两种类型的操作：中间操作(如 filter 或 map) 和 终端操作(如 count、findFirst、forEach、reduce)，中间操作可以连接起来，将一个流转换为另一个流。这些操作不会消耗流，其目的是建立一个流水线。与此相反，终端操作会消耗流，以产生一个最终结果。例如，返回流中的最大元素。它们通常可以通过优化流水线来缩短计算时间。

## 收集器简介

`Collectors.groupingBy` 可以用于分组收集流中的元素，例如：
```java
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class T2 {
    public static void main(String[] args) {
        List<Person> people = Arrays.asList(new Person("a", 10), new Person("b", 11), new Person("c", 10));
        // 按年龄进行分组
        System.out.println(people.stream().collect(Collectors.groupingBy(Person::getAge)));
    }

    static class Person {
        private String name;
        private Integer age;
        public Integer getAge() {
            return age;
        }
        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }
        @Override
        public String toString() {
            return String.format("%s[%s,%d]", getClass().getSimpleName(), name, age);
        }
    }
}
```
执行结果为：
```
{10=[Person[a,10], Person[c,10]], 11=[Person[b,11]]}
```

### 收集器用作高级归约

收集器非常有用，因为用它可以简洁而灵活地定义 collect 用来生成结果集合的标准。

一般来说，Collector 会对元素应用一个转换函数(很多时候是不体现任何效果的恒等转换，例如 toList)，并将结果累积到一个数据结构中，从而产生这一过程的最终输出。我们会在之后研究如何创建自定义收集器。但 Collectors 实用类提供了很多静态工厂方法，可以方便地创建常见收集器的实例，只要拿来用即可。最直接和最常用的收集器是 toList 静态方法，它会把流中的所有元素收集到一个 List 中。

### 预定义的收集器

预定义的收集器，也就是那些可以从 Collectors 类提供的工厂方法(如 groupingBy)创建的收集器。它们主要提供了三大功能：
- 将流元素归约和汇总为一个值；
- 元素分组；
- 元素分区；

## 归约和汇总

但凡要把流中所有的项目合并成一个结果时，就可以使用收集器。这个结果可以是任何类型，可以复杂如多级映射，也可以是一个整数。

`Collectors.summingInt` 可以接受一个把对象映射为求和所需的 int 的函数，并返回i个收集器。该收集器在传递给普通的 collect 方法后即执行我们需要的汇总操作。

### 广义的归约汇总

事实上，我们已经讨论的所有收集器，都是一个可以用 reducing 工厂方法定义的归约过程的特殊情况而已。Collectors.reducing 工厂方法是所有这些特殊情况的一般化。可以说，先前讨论的案例仅仅是为了方便程序员而已（不过，方便程序员和可读性时头等大事）。例如，可以用 reducing 方法创建的收集器来计算你菜单中的总热量。

`Collector<T, ?, U> reducing(U identity,Function<? super T, ? extends U> mapper,BinaryOperator<U> op)`
- 第一个参数是一个归约操作的起始值，也就是流中没有元素时的返回值，所以很显然，对于数值和而言，0是一个合适的值。
- 第二个参数是一个函数，将流中对象转换为目标类型对象；
- 第三个参数将两个目标类型对象合并；

Stream 接口的 collect 和 reduce 方法的不同。语意问题：reduce 方法旨在将两个值结合生成一个新值，它是一个不可变的归约。collect 方法的设计就是要改变容器，从而积累要输出的结果。

Stream 使用的建议：尽可能为手头的问题探索不同的解决方法，但在通用的方案中，始终选择最专门化的一个。无论是从可读性还是性能上看，这一般都是最好的决定。

reduce 和 collect 的差异 (https://stackoverflow.com/questions/22577197/java-8-streams-collect-vs-reduce)：

reduce 是一个 “折叠” 操作，其对应的函数声明为：
```java
// 参数1：初始值为 v = t0，之后对每个元素 t 使用 v += t，最后返回 v
reduce(T t0, java.util.function.BinaryOperator<T>)
// 没有初始值，对每个元素都使用指定二维操作，返回 Optional<T> v
reduce(java.util.function.BinaryOperator<T>)
// 参数1：初始值为 u0，通过 accumulate 函数对每个元素进行转换，使用 opt 函数合并两个结果
reduce(U u0, java.util.function.BiFunction<U,? super T,U> accumulate, java.util.function.BinaryOperator<U> opt)
```

collect 是一个聚集的操作，实现需要重建一个集合，之后每个元素被加入到该集合中(可能存在进一步处理)，其对应的函数声明为：
```java
collect(java.util.function.Supplier<R>, java.util.function.BiConsumer<R,? super T>, java.util.function.BiConsumer<R,R>)
collect(java.util.stream.Collector<? super T,A,R>)
```
reduce 函数的目标是计算一个值，因此其函数参数都是有返回值的 Function。collect 函数的目的是将元素加入一个集合中，因此其函数参数都是 Consumer。当 reduce 的返回值是一个集合时，某种意义上可以实现 collection 的效果。就代码而言，两者可以相互替代，但会使代码阅读者不容易理解，建议如果最终要归约为一个值，使用 reduce，如果最终返回一个集合，则使用 collect。


### 分组

`Collectors.partitioningBy` 一个常见的数据库操作是根据一个或多个属性对集合中的项目进行分组。

### Collectors 类的静态工厂方法

|工厂方法|返回类型|用于|示例|
|---|---|---|---|
|toList|`List<T>`|将流中所有项目收集到一个 List|`List<Dish> dishes = menuStream..collect(toList())`|
|toSet|`Set<T>`|将流中所有项目收集到一个 Set，删除重复项|`Set<Dish> dishes = menuStream..collect(toSet())`|
|toCollection|`Collection<T>`|将流中所有项目收集到给定的供应源创建的集合|`Collection<Dish> dishes = menuStream.collect(toCollection(ArrayList::new))`|
|counting|`Long`|计算流中元素个数|`Long howManyDishes = menuStream.collect(counting())`|
|summingInt|`Integer`|对流中项目的一个整数属性求和|`int totalCalories = menuStream.collect(summingInt(Dish::getCalories))`|
|averagingInt|`Double`|计算流种项目 Integer 属性的平均值|`double avgCaloris = menuStream.collect(averagingInt(Dish::getCalories))`|
|summarizingInt|`IntSummaryStatistics`|收集关于流中项目Integer属性的统计值，如最大、最小、平均、总和||
|join|`String`|连接流中每个项目调用 toString 方法所生成的字符串||
|maxBy|`Optional<T>`|选出最大元素|`menuStream.collect(maxBy(comparingInt(Dish::getCalories)))`|
|minBy|`Optional<T>`|选出最小元素||
|reducing|归约操作产生的类型|从一个作为累加器的初始值开始，利用 BinaryOperator 与流中的元素逐个结合，从而将流归约为单个值|`menuStream.collect(reduce(0, Dish::getCalaries, Integer::sum))`|
|collectingAndThen||包裹另一个收集器，对其结果应用转换函数||
|groupingBy|`Map<K, List<T>>`|分组||
|partitioningBy|`Map<Boolean,List<T>>`|分区||
|mapping||将对象转换||

## 收集器接口

Collector 接口包含了一系列方法，为实现具体的归约操作(即收集器)提供了范本。我们已经看过了 Collector 接口实现的许多收集器，例如 `toList` 或 `groupingBy`。这也意味着，你可以为 Collector 接口提供自己的实现，从而自由地建立自定义归约操作。

Collector 接口定义：
```java
public interface Collector<T, A, R> {
    Supplier<A> supplier();
    BiConsumer<A, T> accumulator();
    BinaryOperator<A> combiner();
    Function<A, R> finisher();
    Set<Characteristics> characteristics();
}
```
T 是流中要收集的项目的泛型；
A 是累加器的类型，累加器是在收集过程中用于累计部分结果的对象。
R 是收集操作得到的对象（通常但不一定是集合）的类型；


例如，你可以实现一个 `ToListCollector<T> ` 类，将 `Stream<T>` 中所有元素收集到一个 `List<T>` 中。它的签名为：
`class ToListCollector<T> implement Collector<T,List<T>,List<T>>`。





