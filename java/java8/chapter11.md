# CompletableFuture 组合式异步编程

分支/合并框架以及并行流是实现并行处理的宝贵工具；它们将一个操作切分为多个子操作，在多个不同的核、CPU甚至的机器上并行地处理这些子操作。

与此相反，如果你的意图是实现并发，而非并行，或者你的主要目标是在同一个CPU上执行几个松耦合的任务，充分利用 CPU 的核，让其足够忙碌，从而最大化程序的吞吐量，那么你真正想做的是避免因为等待远程服务的返回，或者对数据库的查询，而阻塞线程的执行，浪费宝贵的计算资源，因为这种等待可能需要很长的时间。

## Futrue 接口

Futrue 接口在 Java5 中被引入，设计初衷是对将来某个时刻会发生的结果进行建模。它建模了一种异步计算，返回一个执行运算结果的引用，当运算结束后，这个引用被返回给调用方。在 Future 中触发那些潜在耗时的操作，把调用线程解放出来，让它能够继续执行其他有价值的工作，不需要呆呆等待耗时操作的完成。

Future 的另一个优点是它比更底层的 Thread 更易用。要使用 Future ，通常你只需要将耗时的操作封装在一个 Callable 对象中，再将它提交给 ExecutorService 即可。下面这段代码展示了 Java 8 之前使用 Future 的一个例子。
```java
        // 创建 ExecutorService，通过它你可以向线程池提交任务
        ExecutorService executor = Executors.newCachedThreadPool();
        // 向 ExecutorService 提交一个 Callable 对象
        Future<Double> future = executor.submit(new Callable<Double>() {
            @Override
            public Double call() throws Exception {
                // 以异步方式在新的线程中执行耗时操作
                return doSomeLongComputation();
            }
        });
        // 异步操作进行的同时，你可以做其他的事情
        doSomethineElse();
        try {
            // 获取异步操作的结果，如果最终被阻塞，无法得到结果，那么在最多等待 1s 后退出
            Double result = future.get(1, TimeUnit.SECONDS);
        } catch (ExecutionException ee) {
            // 计算抛出一个异常
        } catch (InterruptedException ie) {
            // 当前线程在等待过程中被中断
        } catch (TimeoutException te) {
            // 在 Future 对象完成之前超过已过期
        }
```
