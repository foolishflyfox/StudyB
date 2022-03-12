# 第四章 引入流

## 流是什么

流是 Java API 的新成员，它允许你以声明性方式处理数据集合(通过查询语句来表达，而不是临时写一个实现)。就选择来说，你可以把它们看成遍历数据集的高级迭代器。此外，流还可以透明地并行处理，你无需写任何多线程代码。

使用 Collection 接口需要用户去做迭代(比如用 for-each)，这称为外部迭代。相反 Streams 库使用内部迭代。

## 流操作

### 中间操作

诸如 filter 或 sorted 等中间操作会返回另一个流。这让多个操作可以连接起来形成一个查询。最重要的是，除非流水线上触发一个终端操作，否则中间操作不会执行任何处理 -- 它们很懒。这是因为中间操作一般都可以合并起来，在终端操作时一次性全部处理。

### 终端操作

终端操作会从流的流水线生成结果。其结果是如何不是流的值，比如 List、Integer，甚至于 void。例如下面的例子：`menu.stream().forEach(System.out::println)`。

### 使用流

总而言之，流的使用一般包括3件事：
- 一个数据源(如集合)来执行一个查询；
- 一个中间操作链，形成一条流的流水线；
- 一个终端操作，执行流水线，并生成结果。

流的流水线背后的理念类似于构建器模式。在构建器模式中有一个调用链来设置一套配置(对流来说就是一个中间操作链)，接着是调用 built 方法(对流来说就是终端操作)。



