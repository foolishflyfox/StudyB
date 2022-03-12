# Java 异步编程

## 同步计算与异步计算

从多个任务的角度看，任务可以是串行的，也可以是并发的。从单个任务的角度来看，任务的执行方式可以是同步的(Synchronous)，也可以是异步的(Asynchronous)。

以同步方式执行的任务，我们称为同步任务，其任务的发起与任务的执行时在同一条时间线上的。换而言之，任务的发起与任务的执行是串行的。

以异步方式执行的任务，我们称之为异步任务，其任务的发起与任务的执行是在不同的时间线上进行的。换而言之，任务的发起与任务的执行是并发的。

阻塞与非阻塞只是任务执行方式(同步/异步)本身的一种属性，它与任务执行方式之间没有必然联系：异步任务可能是非阻塞的，也可能是阻塞的(Future.get时被阻塞)，同步任务既可能是阻塞的，也可能是非阻塞的(以轮询方式等待结果)。

同步方式代码简单、直观，但往往意味着阻塞，限制系统的吞吐率。异步方式意味着非阻塞，有利于提高吞吐率，但代码更复杂，需要更多资源投入。

## Java Executor 框架

Runnable 接口和 Callable 接口都是对任务处理逻辑的抽象，这种抽象使得我们无需关心任务的具体处理逻辑。java.util.concurrent.Executor 接口则是对任务的执行进行的抽象。接口仅定义了如下方法：`void execute(Runnable command)`。command 参数代表要执行的任务，Executor 接口使得任务的提交方(相当于生产者)无需关心任务具体的执行细节：比如，任务是采用一个专门的工作者线程执行还是采用线程池执行；采用什么样的线程池执行；多个任务是以何种顺序执行的。

可见，Executor 接口使得任务的提交与任务具体执行难细节解耦(Decoupling)。解耦任务的提交与任务的具体执行细节所带来的好处的一个例子是：它在一定程度上屏蔽任务同步执行与异步执行的差异。

ExecutorService 接口继承自 Executor 接口，ThreadPoolExecutor 是 ExecutorService 的默认实现类。

### 实用工具类

java.util.concurrent.Executors 除了能够返回默认线程工厂(Executors.defaultThreadFactory())、能够将 Runnable 实例转换为 Callable 实例外(Executors.callable方法)之外，还提供了一些能够返回ExecutorService 实例的快捷方法。

- ExecutorService Executors.newCachedThreadPool: 适合用于执行大量耗时较短且比较频繁的任务。如果提交的任务耗时较长，那可能导致线程池中的工作者线程无限制增长，最后导致过多的上下文切换，从而使整个系统变慢；创建线程池的方式为 `ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());`。一个线程在执行SynchronousQueue.take()而被暂停，那么 SynchronousQueue.offer(E) 调用会直接返回 false，即入队列失败。因此，在该线程池中的所有工作者线程都在执行任务，即无空闲工作者线程的情况下给其提交任务会导致该任务无法被缓存成功。而 ThreadPoolExecutor 在任务缓存失败且线程池未达到最大线程池大小的情况下会创建并启动新的工作者线程(如果达到，则 提交任务将抛异常)。在极端情况下会，该线程池每提交一个任务就会导致一个新的工作者线程被创建并启动，这最终会导致系统中的线程过多，从而导致过多的上下文切换而使整个系统被拖慢。因此，Executors.newCachedThreadPool()所返回的线程池适合于用来执行大量耗时较短且提交频率较高的任务。而提交频率较高，且耗时较长的任务(尤其包含阻塞操作的任务)则不适合用该线程池来执行。
- ExecutorService Executors.newFixedThreadPool: 由于该方法返回的核心线程大小等于最大线程池大小，因此，该线程中的工作者线程永远不会超时；我们必须在不需要该线程池时主动将其关闭。
- ExecutorService Executors.newSingleThreadExecutor: 适合用来实现单(多)生产者-单消费者模式。该方法的返回值为法被转换为 ThreadPoolExecutor 类型。

### 异步任务的批量执行：CompletionService

尽管 Future 接口使我们能够方便地获取一步的处理结果。但是，如果需要在一次性提交一批异步任务并获取这些任务的处理结果的话，那么仅使用 Future 接口写出来的代码将颇为繁琐。

java.util.concurrent.CompletionService 接口为异步任务的批量提交以及获取这些任务的处理结果提供了便利。

CompletionService 接口定义了一个 submit 方法可用于提交异步任务，该方法的签名为与 ThreadPoolExecutor 的一个 submit 方法相同：`Future<V> submit(Call<V> task)`。task 参数代表待执行的异步任务，该方法的返回值可用于获取相应异步任务的处理结果。若要获取批量提交的异步任务的处理结果，我们可以用 CompletionService 专门为此定义的方法，其中一个方法是 `Future<V> task() throws InterruptedException`。该方法与 BlockingQueue.take() 类似，是一个阻塞方法，其返回值是一个已经执行结束的异步任务对应的 Future 实例，该实例就是提交任务时 `submit(Callable<V>)` 调用的返回值。

如果 take() 被调用时没有已经执行结束的异步任务，那么 take() 的执行线程就会被停止，直到有异步任务执行结束。因此，我们批量提交了多少个异步任务，则多少次连续调用 CompletionService.take() 便可以获取这些任务的处理结果。

java 标准库提供了 CompletionService 接口的实现类是 ExecutorCompletionService。ExecutorCompletionServices 的一个构造器是：`ExecutorCompletionService(Executor executor, BlockingQueue<Future<V>> completionQueue)`。由此可见，ExecutorCompletionService 相当于 Executor 实例与 BlockingQueue 实例的一个融合体。其中，Executor 实例负责接收并处理异步任务，而 BlockingQueue 实例则用于存储已经执行完毕的异步任务对应的 Future 实例。ExecutorCompletionService 会为其客户端提交的每个异步任务(Callable 实例或者 Runnable 实例)都创建一个相应的Future实例。通过该实例其客户端代码便可以获得相应异步任务的处理结果。

ExecutorCompletionService 每执行完一个异步任务，就将该任务对应的 Future 实例存入其内部维护的 BlockingQueue 实例之中，而其客户端代码则可以通过 ExecutorCompletionService.take() 调用来获取这个 Future 实例。

使用 ExecutorCompletionService 的另一个构造器 ExecutorCompletionService(Executor executor)创建实例相当于：`new ExecutorCompletionService<V>(executor, new LinkedBlockingQueue<Future<V>>())`。

例如下面的代码：
```java
public class T5 {
    @SneakyThrows
    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        int[] a = {5,2,7,1,3,4,9,8,6};
        List<Future<Integer>> resultFutures = new ArrayList<>();
        for(int v: a) {
            Future<Integer> submit = executorService.submit(() -> {
                Tools.sleep(v*1000);
                return v;
            });
            resultFutures.add(submit);
        }
        
        // 一个任务执行完毕，就取出结果并将其打印
        while (!resultFutures.isEmpty()) {
            List<Future<Integer>> newResultFutures = new ArrayList<>();
            for (Future<Integer> resultFuture : resultFutures) {
                if(resultFuture.isDone()) {
                    System.out.printf("%s : %d\n", System.currentTimeMillis(), resultFuture.get());
                } else {
                    newResultFutures.add(resultFuture);
                }
            }
            resultFutures = newResultFutures;
        }
        executorService.shutdown();
    }
}
```
执行结果为：
```java
1637410949318 : 1
1637410950318 : 2
1637410951317 : 3
1637410952320 : 4
1637410953318 : 5
1637410954318 : 6
1637410955318 : 7
1637410956319 : 8
1637410957320 : 9
```
如果用 CompleteService，要实现相同的功能就要简单一些了：
```java
public class T6 {
    @SneakyThrows
    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(10);
        CompletionService<Integer> service = new ExecutorCompletionService<>(executor);
        int[] a = {5,2,7,1,3,4,9,8,6};
        for(int v: a) {
            service.submit(() -> {
                Tools.sleep(v*1000);
                return v;
            });
        }

        for (int i=0; i<a.length; ++i) {
            System.out.printf("%s : %d\n", System.currentTimeMillis(), service.take().get());
        }
        executor.shutdown();
    }
}
```

`ExecutorService.invokeAll(Collection<? extends Callable<T>> task)` 也能够用来批量提交异步任务，该方法能够并发执行 tasks 参数所指定的一批任务，但是该方法只有在 tasks 参数所指定的一批任务中的所有任务都执行结束后才返回，其返回值是包含各个任务对应的 Future 实例的列表(List)。因此，使用 invokeAll 方法提交批量任务的时候，任务提交方等待 invokeAll方法发挥的时间取决于这批任务中最耗时的任务的执行耗时。

例如下面的代码：
```java
public class T7 {
    public static void main(String[] args) throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        List<Callable<Integer>> callables = Stream.of(3, 5, 4).map(v -> new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                Tools.sleep(v * 1000);
                return v;
            }
        }).collect(Collectors.toList());
        System.out.println(System.currentTimeMillis() + " start");
        List<Future<Integer>> futures = executorService.invokeAll(callables);
        System.out.println(System.currentTimeMillis() + " after invokeAll");
        System.out.println(System.currentTimeMillis() + ": " +
                futures.stream().map(f -> {
                    try {
                        return f.get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                    return null;
                }).collect(Collectors.toList()));
        executorService.shutdown();

    }
}
```
执行的结果为：
```java
1637423480100 start
1637423487110 after invokeAll
1637423487110: [3, 5, 4]
```
由于只要两个工作线程，因此第一个线程需要执行两个任务，时长为7s，因此 invokeAll 在 7s 之后才返回。

## 异步计算助手 FutureTask

无论是 Runnable 实例还是 Callable 实例所表示的任务，只要我们将其提交给线程池执行，那么这些任务就是异步任务。采用 Runnable 实例来表示异步任务，其优点是任务既可以交给一个专门的工作者线程执行，也可以交个一个线程池或者 Executor 的其他实现类来执行；其缺点是我们无法直接获取任务的执行结果。

使用 Callable 实例来表示执行异步任务，其优点是我们可以通过 `ThreadPoolExecutor.submit(Callable<T>)` 的返回值获取任务的执行结果；其缺点是 Callable 实例表示的异步任务只能交给线程池执行，而无法直接交给一个专门的工作者线程或者Executor实现类执行。因此，使用 Callable 实例来表示异步任务会使任务执行方式的灵活性大为受限。

java.util.concurrent.FutureTask 类融合了 Runnable 接口和 Callable 接口的优点：FutureTask 是 Runnable 接口的一个实现类，也可以交给 Executor 实例(如线程池)执行；FutureTask 还可以直接返回其代表的异步任务的处理结果。

`ThreadPoolExecutor.submit(Callable<T> task)` 的返回值就是一个 FutureTask 实例。FutureTask 是 java.util.concurrentRunnableFuture 接口的一个实现类。由于 RunnableFuture 接口继承了 Future 和 Runnable 接口，因此 FutureTask 即是 Runnable 接口的实现类，也是 Future 的接口的实现。FutureTask 的一个构造器可以将 Callable 实例转换为 Runnable 实例，该构造器的声明为：`public FutureTask(Callable<T> callable)`。

该构造器使得我们能够方便地创建一个能够返回处理结果的异步任务。我们可以将任务的处理逻辑封装在一个 Callable 实例中，并以该实例为参数创建一个 FutureTask 实例，由于 FutureTask 类实现了 Runnable 接口，因此上述构造器的作用就相当于将 Callable 实例转换为 Runnable 实例，而 FutureTask 实例本身也代表了我们要执行的任务。

例如：
```java
public class T8 {
    public static void main(String[] args) throws Exception{
        FutureTask<Integer> task = new FutureTask<>(() -> {
            Tools.sleep(3000);
            return new Random().nextInt(30);
        });
        System.out.println(System.currentTimeMillis() + " : start");
        new Thread(task).start();
        Integer result = task.get();
        System.out.println(System.currentTimeMillis() + " : v = " + result);
    }
}
```
执行结果为：
```
1637425241427 : start
1637425244430 : v = 28
```
FutureTask 还支持以回调(Callback)的方式处理任务的执行结果。当 FutureTask 实例所代表的任务执行结束后，FutureTask.done() 会被执行。FutureTask 是个 protected 方法，FutureTask 的子类可以覆盖该方法并在其中实现对任务执行结果的处理。

FutureTask.done() 中的代码可以通过 FutureTask.get() 调用来获取任务的执行结果，此时，由于子任务已经执行结束，因此 FutureTask.get() 调用不会使得当前线程暂停。但是，由于任务执行线程结束既包括正常终止，也包括异常终止以及任务被取消而终止，因此 FutureTask.done() 方法中执行的代码可能需要在调用 FutureTask.get() 前调用 FutureTask.isCancelled() 来判断任务是否被取消，以免 FutureTask.get() 调用抛出 CancellationException 异常(运行时异常)。通过回调完成上述功能代码为：
```java
public class T9 {
    public static void main(String[] args) throws Exception{
        FutureTask<Integer> task = new FutureTask<Integer>(() -> {
            Tools.sleep(3000);
            return new Random().nextInt(30);
        }){
            @Override
            protected void done() {
                try {
                    Integer result = this.get();
                    System.out.println(System.currentTimeMillis() + " : v = " + result);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        };
        System.out.println(System.currentTimeMillis() + " : start");
        new Thread(task).start();
    }
}
```

### 可重复执行的异步任务

FutureTask 基本上被设计成用来表示一次性执行的任务，其内部会维护一个表示任务运行状态(包括未开始运行、已经运行结束等)的状态变量，FutureTask.run() 在执行任务处理逻辑前会先判断相应任务的运行状态，如果该任务已经被执行过，那么 FutureTask.run() 会直接返回(并不会抛异常)。
```java
public class T10 {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        FutureTask<Integer> futureTask = new FutureTask<>(() -> {
            Tools.sleep(1000);
            int r = new Random().nextInt(100);
            System.out.println(System.currentTimeMillis() + " : result = " + r);
            return r;
        });
        new Thread(futureTask).start();
        System.out.println(System.currentTimeMillis() + " : " + futureTask.get());
        new Thread(futureTask).start();
        System.out.println(System.currentTimeMillis() + " : " + futureTask.get());
    }
}
```
结果为：
```java
1637454596927 : result = 5
1637454595921 : 5
1637454596928 : 5
```
因此，FutureTask 实例所代表的任务是无法被重复执行的。这意味着同一个 FutureTask 实例不能多次提交给 Executor 实例执行。类似的 Runnable 会多次执行：
```java
public class T11 {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Runnable runnable = () -> {
            Tools.sleep(1000);
            int r = new Random().nextInt(100);
            System.out.println(System.currentTimeMillis() + " : result = " + r);
        };
        new Thread(runnable).start();
        new Thread(runnable).start();
    }
}
```
执行结果为：
```
1637454820147 : result = 77
1637454820147 : result = 65
```

FutureTask.runAndReset() 能够打破这种限制，使得一个 FutureTask 实例所代表的任务能够被多次执行。FutureTask.runAndReset() 是一个 protected 方法，它能够执行 FutureTask 实例代表的任务但是不记录任务的处理结果。因此，如果一个对象所代表的任务需要被执行多次，并且我们需要任务每次的执行结果进行处理，那么 FutureTask 仍然是不适用的，此时我们可以考虑使用抽象异步任务类 AsyncTask 来表示这种任务。

AsyncTask 抽象类同时实现了 Runnable 接口和 Callable 接口。AsyncTask 子类通过覆盖 call 方法来实现其任务处理逻辑，而 AsyncTask.run() 则充当任务处理逻辑的执行入口。AsyncTask 实例可以提交给 Executor 实例执行。当任务执行成功结束后，相应 AsyncTask 实例的 onResult 方法会被调用以处理任务的执行结果。当任务执行过程中抛出异常时，相应的 AsyncTask 实例的 onError 方法会被调用以处理这个异常。

AsyncTask 的子类可以覆盖 onResult 方法、onError 方法来对任务执行结果、任务执行过程中抛出的异常进行处理。由于 AsyncTask 在回调 onResult、onError 方法的时候不是直接调用，而是通过向 Executor 实例提交一个任务进行的，因此 AsyncTask 的任务执行(即 AsyncTask.run()调用)可以是在一个工作者线程中进行的，而对任务只信你个结果的处理则可以在另一个线程中进行，这就从整体上实现了任务的执行和堆任务执行结果的处理的并发：设 asyncTask 为一个任意 AsyncTask 实例，当一个线程在执行 asyncTask.onResult 方法处理 asyncTask 一次执行的执行结果时，另一个工作者线程可能正在执行 asyncTask.run()，即进行 asyncTask() 的下一次执行。 

## execute vs submit

execute 和 submit 都属于线程池的方法，execute 只能提交 Runnable 类型的任务，而 submit 既能提交 Runnable 类型任务，也能提交 Callable 类型任务。execute 会直接抛出任务执行时的异常，submit 会吃掉异常，可通过 Future.get 方法将任务执行时的异常重新抛出。

execute 所属顶层接口为 Executor，submit 所属顶层接口为 ExecutorService，实现类ThreadPoolExecutor重写了execute方法,抽象类AbstractExecutorService重写了submit方法。
## 计划任务

在有些情况下，我们可能需要事先提交一个任务，这个任务并不是立即被执行的，而是要在指定的时间或者周期性地被执行，这种任务就成为计划任务(Scheduled Task)，典型的计划任务包括清理系统垃圾数据、系统监控、备份数据等。

ExecutorService 接口的子类 ScheduledExecutorService 接口定义了一组方法用于执行计划任务。ScheduleExecutorService 接口的默认实现类为 java.util.concurrent.ScheduleThreadPoolExecutor 类，它是 ThreadPoolExecutor 的一个子类。Executors 除了提供创建 ExecutorService 实例的便捷工厂外，还提供了两个静态工厂方法用于创建 ScheduledExecutorService 实例：
```java
public static ScheduledExecutorService newScheduledThreadPool(int corePoolSize);
public static ScheduledExecutorService newScheduledThreadPool(int corePoolSize, ThreadFactory threadFactory);
```
ScheduleExecutorService 接口定义的方法按其功能可分为以下两种：
- 延迟执行提交的任务，包括以下两个方法
    - `<V> ScheduleFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit)`
    - `ScheduleFuture<?> schedule(Runnable command, long delay, TimeUnit unit)`
- 周期性地执行提交的任务，包括以下两个方法：
    - `ScheduledFuture<?> scheduleAtFixedRate(Runnable command,long initialDelay,long period,TimeUnit unit);`
    - `public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command,long initialDelay,long delay,TimeUnit unit);`

