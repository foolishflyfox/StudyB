# 第一章 为什么要关心 Java 8

Java8 提供了一个新的 API(称为“流”，Stream)，它支持许多处理数据的并行操作，其思路和在数据库查询语言中的思路类似--用更高级的方式表达想要的东西，而由“实现”(在这里是 Stream 库)来选择最佳低级执行机制。这样就可以避免用 synchronized 编写代码，这一代码不仅容易出错，而且在多核 CPU 上执行所需的成本也比你想象的要高。

从有点修正主义的角度看，在 Java 8 中加入 Stream 可以看作把另外两项扩充加入 Java 8 的直接原因：把代码传递给方法的简洁方式(方法引用、Lambda)和接口中的默认方法。

Java 8 中将代码传递给方法的功能(同时也能够返回代码，并将其包含在数据结构中)还让我们能够使用一整套新技巧，通常称为函数式编程。一言以蔽之，这种被函数式编程界称为函数的代码，可以被来回传递并加以组合，以产生强大的编程语汇。

## 流处理

Java 8 在 java.util.stream 中添加了一个 Stream API；`Stream<T>` 就是一系列 T 类型的项目。Stream API 的很多方法可以链接起来形成一个复杂的流水线，就像用管道链接的 Unit 命令一样。

推动这种做法的关键在于，现在你可以在一个更高的抽象层次上写 Java 程序了：思路变成了把这样的流变成那样的流(就像写数据库查询语句时的那种思路)，而不是一次只处理一个项目。另一个好处是，Java 8 可以透明地把输入的不相关部分拿到几个 CPU 内核上去分别执行你的 Stream 操作流水线 -- 这几乎是免费的并行，用不着去费劲搞 Thread 了。

## 并行与共享的可变数据

上面说到几乎免费的并行，需要作出的改变是：对传给流方法的行为的写法稍作改变。这种改变可能一开始会让你感觉不舒服，但一旦习惯上你就会爱上它们。

你的行为必须能够同时对不同的输入安全地执行。一般情况下这意味着，你写代码时本能个访问共享的可变数据。这些函数有时被称为“纯函数”或“无副作用函数”或“无状态函数”。

Java8 的流水线并行比 Java 现有的线程 API 更容易，因此，尽管可以使用 synchronized 来打破“不能也有共享的可变数据”这一规则，但这相当于是在和整个体系作对，因为它使所有围绕这一规则做出的优化都失去了意义。在多个处理器内核之间使用 synchronized ，其代价往往比你预期的要大得多。

没有共享的可变数据，将代码传递给其他方法的能力是函数式编程范式的基石。

## Java 中的函数

编程语言中的函数，通常指方法，尤其是静态方法。Java 8 中新增了函数 —— 值的一种新形式。

Java 程序可能操作的值：
- 原始值，如 int、long等；
- 对象；
- 方法；

## 流

通过流处理的并行话，可以大大减少计算时间，例如：
```java
import java.util.HashSet;
import java.util.Set;
import java.util.stream.LongStream;

public class ParallelStream {
    public static void main(String[] args) {
        Set<String> threadNames1 = new HashSet<>();
        Long t0 = System.currentTimeMillis();
        Long result = LongStream.range(0L, 1000000000L)
                .parallel()
                .map(v -> {
                    threadNames1.add(Thread.currentThread().getName());
                    return (v % 33 == 0) ? 1 : 0;
                }).sum();
        Long t1 = System.currentTimeMillis();
        System.out.printf("result = %d, %d threads, cost %d ms\n",
                result, threadNames1.size(), t1 - t0);
    }
}
```
多次执行的结果为：
```
result = 30303031, 29 threads, cost 2510 ms
result = 30303031, 20 threads, cost 4162 ms
result = 30303031, 29 threads, cost 3643 ms
result = 30303031, 21 threads, cost 3603 ms
result = 30303031, 18 threads, cost 1697 ms
result = 30303031, 42 threads, cost 1799 ms
```
可以看到，每次执行所用的线程数量并不相同，并且也并不是线程数越多，执行时间越短。如果我们将 `.parallel` 这一行去掉，多次执行的结果为：
``` 
result = 30303031, 1 threads, cost 6892 ms
result = 30303031, 1 threads, cost 6876 ms
result = 30303031, 1 threads, cost 6885 ms
result = 30303031, 1 threads, cost 6891 ms
result = 30303031, 1 threads, cost 6809 ms
result = 30303031, 1 threads, cost 6837 ms
```
可以看到，每次执行的线程数量为 1 个，并且执行用时都很稳定，比多线程用时要长。

## 默认方法

Java 8 加入默认方法主要是为了支持库设计师，让他们能够写出更容易改进的接口。因为为接口添加新的方法时，需要修改所有该接口的实现类，但是，该接口被哪些类实现是不可控的，因此，接口提供默认方法，使接口的实现类不用修改代码就能编译通过。

# 第三章 lambda 表达式

lambad 表达式复合：

- 比较器复合：`Comparator.comparing(Person::getAge).thenComparing(Person::getSalary).reversed()`;
- 谓语复合
```java
        List<Person> people = Lists.newArrayList();
        Predicate<Person> young = p -> p.getAge() < 40;
        Predicate<Person> rich = p -> p.getSalary() > 100000;
        people.stream().filter(young.and(rich)); // 选出年轻且有钱的人
        people.stream().filter(young.or(rich));  // 选出年轻或有钱的人
        people.stream().filter(rich.negate());  // 选出穷人
```
- 函数复合
```
        Function<List<Integer>, Integer> sum = list -> list.stream().mapToInt(v -> v).sum();
        Function<Integer, Integer> minusTen = v -> v-10;
        Function<Integer, Integer> doubleTime = v -> v*2;
        Function<Integer, String> hexString = v -> String.format("%x", v);
        List<Integer> a = Lists.newArrayList(1,2,3,4,5,6);
        Function<List<Integer>, String> f = sum.andThen(minusTen).andThen(doubleTime).andThen(hexString);
        System.out.println(f.apply(a));  // 返回 16
        Function<List<Integer>, String> f2 = doubleTime.compose(sum.andThen(minusTen)).andThen(hexString);
        System.out.println(f2.apply(a)); // 返回 16
```

