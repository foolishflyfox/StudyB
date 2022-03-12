# Stream 集成器源码分析

Stream 的 collector 用于将数据流归约到一个对象，其参数类型为：`Collector<? super T, A, R> collector`，其中 T 为流中的元素类型，A 为中间变量的类型，R 表示归约后对象的类型。`Collector` 的定义为：
```java
public interface Collector<T, A, R> {
    /**
     * 创建一个提供器，通过该提供器可以生成一个可修改的，存放结果的容器
     */
     Supplier<A> supplier();

     /**
      * 将流中的一个元素整合到中间变量中的处理函数
      */
    BiConsumer<A, T> accumulator();

    /**
     * 返回合并两个中间变量a、b的处理函数，处理过程可以是将a合并到b中，返回b，也可以返回一个新生成的中间变量
     */
    BiOperator<A> combiner();

    /**
     * 提供将中间变量转换为结果的转换函数
     */
    Function<A, R> finisher();

    /**
     * 返回 Collector.Characteristics 类型的集合，表示收集器的特性
     */
    Set<Characterstics> characteristics();

    /**
     * 指明收集器的属性，流的归约实现可根据该属性进行处理优化
     */
    enum Characterstics {
        /**
         * 表示该集成器是并行的，表示支持并行调用 accumulator 提供的处理函数
         * 如果只设置了 CONCURRENT，没有设置 UNORDERED 特性，就意味着该收集器只会在应用到一个无序数据源(如 Set)时，才会被并发收集
         */
        CONCURRENT,
        /**
         * 代表这个收集器在执行 accumulator 时不会保留它遇到的元素的顺序，如果容器 A 中不需要保存 T 的顺序，可以设置该枚举值
         */
        UNORDERED,
        /**
         * 表示 finisher 函数为 identity 函数，可以直接将 A 类型转换为 R 类型
         */
        IDENTITY_FINISH
    }
}
```
