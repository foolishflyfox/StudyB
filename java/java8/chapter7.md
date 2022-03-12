# 并行数据处理与性能

新的 Stream 接口意义让你以声明性方式处理数据集。将外部迭代替换为内部迭代能够让原生 Java 库控制流元素的处理，这种方法让 Java 程序员无需显式实现优化来为数据集的处理加速。到目前为止，最重要的好处是可以对这些集合执行操作流水线，能够自动利用计算机上的多个内核。

例如，在 java7 之前，并行处理数据集合非常麻烦：
1. 明确把包含数据的数据结构划分成子部分；
2. 给每个子部分分配一个独立的线程；
3. 通过同步机制避免竞争；
4. 合并结果；

Stream 接口允许以声明式地将顺序流变为并行流。

## 并行流

通过对收集源调用 parallelStream 方法把集合转换为并行流。并行流就是一个把内容分成多个数据块，并用不同的线程分别处理每个数据块的流。这样你就可以自动把给定操作的工作负荷分配给多核处理器的所有内核，让它们都忙起来。

注意，对顺序流调用 parallel 方法并不意味着流本身有任何实际的变化。它在内部实际上就是设置了一个 boolean 标志，表示你想让调用 parallel 之后进行的所有操作都并行执行。类似的，你只需要对并行流调用 sequential 方法就可以将它变为顺序流。

注意：你可能以为把两个方法结合起来，就可以更细致地控制在遍历流时哪些操作要并行执行，哪些要顺序执行。例如：
```java
stream.parallel().filter(...).sequential().map(...).parallel().reduce();
```
但最后一次 parallel 或 sequential 调用会影响整个流水线。在上面的例子中，流水线会并行执行，因为最后调用的是 parallel。

```java
    @Test
    public void test7_2() {
        int n = 1000;
        // 至于最后一个 sequential / parallel 设置生效
        System.out.println(getStream(n).parallel().map(v -> v*2).sequential().filter(v -> v%3==0).isParallel());
        // 只有一个元素的 set
        System.out.println(getStream(n)
                .parallel().map(v -> Thread.currentThread().getName())
                .sequential().map(String::toUpperCase).collect(Collectors.toSet()).size());
        // 有多个元素的 set，数量为计算机的核数
        System.out.println(getStream(n)
                .sequential().map(v -> Thread.currentThread().getName())
                .parallel().map(String::toUpperCase).collect(Collectors.toSet()).size());
    }
```
并行流内部使用了默认的 ForkJoinPool (分支/合并框架)，它默认的线程数是你的处理器数量，这个值可以由 `Runtime.getRuntime().availableProcessors()` 获取。

并行化并不是没有代价的。并行化过程本身本身需要对流做递归划分，把每个子流的归纳操作分配到不同的线程，然后把这些操作的结果合成一个值。但在多个内核之间移动数据的代价可能比你想象的要大，所以很重要的一点是要保证在内核中并行执行工作的时间比在内核之间传输数据的时间长。总而言之，很多情况下不可能或不方便并行化。

### 高效使用并行流

- 如果有疑问，测量：将顺序流转成并行流并不一定是好事。建议先用特定的基准测试检查其性能。
- 留意装箱。
- 有些操作本身在并行流上的性能就比顺序流差。特别是 limit 和 findFirst 等依赖于元素顺序的操作。它们在并行流上执行的代价非常大。你总是可以调用 unordered 方法将有序流变为无序流。那么如果你需要流中的 n 个元素，而不是专门前 n 个的话，对无序并行流到用 limit 可能比单个有序流更高效。
- 还要考虑流的操作流水线的总计算成本。
- 对于较小的数量，选择并行流几乎从来不是好的决定。
- 要考虑流背后的数据结构是否容易分解。
- 要考虑合并的代价高不高。

## 分支/合并框架

分支/合并框架的目的是以递归方式将可以并行的任务拆分成更小的任务，然后将每个子任务的结果合并成整体结果。他是 ExecutorService 接口的一个实现，它把子任务分配给线程池（ForkJoinPool）的工作线程。

### 使用 RecursiveTask

要把任务提交到这个池，必须创建 `RecursiveTask<R>` 的一个子类，其中 R 是并行化任务产生的结果类型，或者如果任务不返回结果，则是 RecursiveAction 类型。要定义 RecursiveTask 只需要实现它唯一的抽象方法 compute: `protected abstract R compute()`。

