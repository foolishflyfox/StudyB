# 使用流

在上一章中你已经看到，流让你conning外部迭代转向内部迭代。这样你就不用写代码来显式地管理数据集合的迭代了(外部迭代)。这种处理数据的方式很有用，因为你让 Stream API 管理如何处理数据，这样 Stream API 就可以在背后进行多种优化了。此外，使用内部迭代的话就办不到了，因为你只能单一线程挨个迭代。

在本章中，你将会看到 Stream API 支持的许多操作。这些操作能让你快速完成复杂的数据查询，如筛选、切片、映射、查找、匹配和规约。接下来，我们将看到一些特殊的流：数值流、来自文件和数组等多种来源的流，最后是无限流。

## 筛选和切片

### 用谓词筛选

Streams 接口支持 filter 方法。该操作会接受一个谓词(一个返回 boolean 的函数)作为参数，并返回一个包括所有符合谓词的元素的流。

### 筛选各异的元素

流还支持一个叫做 `distinct` 的方法，它会返回一个元素各异(根据流所生成元素的 hashCode和equals方法实现)的流。

### 截短流

流支持 `limit(n)` 方法，该方法会返回一个不超过给定长度的流。所需的长度作为参数传递给 limit。如果流是有序的，则最多会返回前n个元素。

### 跳过元素

流还支持 `skip(n)` 方法，返回一个扔掉了前 n 个元素的流。如果流中元素不足 n 个，则返回一个空流。

## 映射

一个非常常见的数据处理套路就是从某些对象中选择信息。

### 对流中年每个元素应用函数

流支持 map 方法，它会接收一个函数作为参数，该函数会被应用到每个元素上，并将其映射成一个新的元素。

### 流的扁平化

flatMap

## 查找和匹配

### 检查谓词是否至少匹配一个元素

anyMatch 返回一个 boolean，属于一个终端操作。

### 检查谓语是否匹配所有元素

allMatch。

noneMatch。

### 查找元素

findAny 方法将返回当前流中的任意元素。

### 查找第一个元素

有些流是有出现顺序来指定流中项目出现的逻辑顺序(比如由 List 或者排好序的数据列生成的流)。对于这种流，你可能想要找到第一个元素。为此，有一个 findFirst 方法，它的工作方法与 findAny 类似。

## 归约

```java
public class T1 {
    public static void main(String[] args) throws InterruptedException{
        List<String> ss = Arrays.asList("abcd", "bcde", "cd", "d");
        System.out.println(ss.stream().flatMap(s -> Arrays.stream(s.split("")))
                .reduce(new LinkedHashMap<String, Integer>(), (mp, c) -> {
                    mp.put(c, mp.getOrDefault(c, 0)+1);
                    return mp;
                }, (mp1, mp2) -> {
                    for (String s : mp2.keySet()) {
                        mp1.put(s, mp1.getOrDefault(s, 0)+mp2.getOrDefault(s, 0));
                    }
                    return mp1;
                }).entrySet().stream()
                .sorted(Comparator.comparing(Map.Entry<String, Integer>::getValue).reversed())
                .collect(Collectors.toList())
        );
    }
}
```
输出为:
```
[d=4, c=3, b=2, a=1, e=1]
```

## 数值流

### 原始类型流特化

Java 8 引入了三个原始类型特化流接口来解决求和问题：IntStream、LongStream 和 DoubleStream，分别将流中的元素特化为 int、long 和 double，从而避免了暗含的装箱成本。每个接口都带来了进行常用数值归约的新方法，比如 sum、max。此外，还有在必要时将它们转换回流对象的方法。这些特化的原因不在于流的复杂性，而是装箱带来的复杂性。

通过 mapToInt、mapToLong、mapToDouble 完成流的特化。

通过 box 可以将特化的数值流转回。

### 数值范围

和数字打交道时，有一个常用的东西是数值范围。比如，假设你想要生成1和100之间所有数字。Java 8 引入两个可以用于 IntStream 和 LongStream 的静态方法，帮助生成这种范围：range 和 rangeClose。这两个方法都是第一个参数接受起始值，第二个参数接受结束值。但 range 是不包含结束值的，而 rangeClose 包含。

### 由值创建流

Stream.of 静态方法通过显式的方式创建一个流。它可以接受任意数量的参数。Stream.empty 将返回一个空流。

`System.out.println(Stream.of(1,2,3).reduce(Integer::sum));` 输出为 `Optional[6]`。

### 由数组创建流

可以使用静态方法 Arrays.stream 从数组创建一个流。`System.out.println(Arrays.stream(new int[]{1,2,3}).sum());` 返回6。

### 由文件生成流

例如，统计文件 data.txt 中的单词数量：
```java
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Stream;

public class T1 {
    public static void main(String[] args) {
        try (Stream<String> lines = Files.lines(Paths.get("data.txt"), Charset.defaultCharset())) {
            System.out.println(lines.flatMap(line -> Arrays.stream(line.split("\\s+")))
                    .reduce(new HashMap<String, Integer>(),
                            (map, v) -> { map.put(v, map.getOrDefault(v, 0)+1); return map;},
                            (m1, m2) -> { 
                                m2.keySet().forEach(k -> m1.put(k, m1.getOrDefault(k, 0)+m2.get(k)));
                                return m1;
                                }));
        } catch(IOException e) {}
    }
}
```
执行结果为：
```
$ cat data.txt
a bb c 
ddd a bb
a c
$ java T1
{bb=2, a=3, c=2, ddd=1}
```

### 由函数生成流

Stream API 提供了两个静态方法来从函数生成流：Stream.iterate 和 Stream.generate。这两个操作都可以创建无限流。

使用: `Stream.iterate(0, n -> n+2).forEach(System.out::println);` 。

生成斐波那契数列：
```java
public class T1 {
    public static void main(String[] args) {
        System.out.println(Stream.iterate(new int[]{0, 1}, v -> new int[]{v[1], v[0]+v[1]})
            .map(a -> a[0]).limit(10).collect(Collectors.toList()));
    }
}
```
generate 方法不是依次对每个新生成的值应用函数，它接受一个 `Supplier<T>` 类型的 Lambda 提供新的值。类似于 python 的 yield 。

同样的斐波那契数列通过 generate 生成为：
```java
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class T1 {
    public static void main(String[] args) {
        System.out.println(Stream.generate(new Supplier<Integer>() {
            private Integer a = 0;
            private Integer b = 1;
            @Override
            public Integer get() {
                b = a + b;  // b1 = a0 + b0
                a = b - a;  // a1 = a0 + b0 - a0
                return b - a;  // a0 + b0 - b0
            }
        }).limit(10).collect(Collectors.toList()));
    }
}
```


