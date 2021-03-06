# 牛刀小试：玩转线程

## 挖掘可并发点

要实现多线程编程的目标——并发计算，我们实现需要找到程序中哪些处理是可以并发化，即由串行改为并发。这些可并发的处理称为可不发点。

## 分而治之

挖掘出程序中可并发点相当于为线程找到了用武之地，接下来便是考虑如何让线程奔赴战场。这时可以利用我们的“老”武器 -- 分而治之。

使用分而治之的思想进行多线程编程，我们首先需要将程序算法中只能串行的部分与可以并发的部分区分开来，然后使用专门的线程(工作者线程)去并发地执行那些可并发化的部分(可并发点)。具体来说，多线程编程中分而治之的使用主要有两种方式：1. 基于数据的分割；2、基于任务的分割。前者从数据入手，将程序的输入数据分解为若干规模较小的数据，并利用若干工作者线程并发处理这些分解后的数据。后者从程序的处理任务(步骤)入手，将任务分解为若干子任务，并分配若干工作者线程并发执行这些子任务。

## 基于数据的分割实现并发化

如果程序的原始输入数据的规模比较大，比如要从几百万条日志记录中统计我们所需的信息，可以采用基于数据的分割。基本思想是将原始输入数据按照一定规则分解为若干规模较小的子输入，并使用工作者线程来对这些子输入进行处理，从而实现对输入数据的并发处理。对于子输入的处理，我们称之为子任务。因此，基于数据的分割的结果是产生一批子任务，这些子任务由专门的工作者线程负责执行。


## 基于任务的分割实现并发化

为了提高任务的执行效率，我们可能使用多个线程去共同完成一个任务的执行。这就是基于任务的分割，其基本思想是将原始任务按一定的规则分解为若干子任务，并使用专门的工作者线程去执行这些子任务，从而实现任务的并发执行。

基于任务的分解可以分为按任务的资源属性分割和按处理步骤分割两种。

### 按任务的资源消耗属性分割

线程所执行的任务按照其消耗的主要资源，可以划分为CPU密集型(CPU-intensive)任务和I/O密集型(I/O-intensive)任务。执行这些任务的线程也相应地被称为 CPU 密集型线程和 I/O 密集型线程。

CPU密集型任务执行过程中消耗的主要资源是CPU时间，CPU密集型任务的一个典型例子是加密和解密。I/O 密集型任务执行过程中你那个消耗的主要资源是I/O资源(如网络和磁盘等)，典型的I/O密集型任务包括文件读写、网络读写等。

一个线程所执行的任务实际上往往同时兼具 CPU 密集型任务和 I/O 密集型任务特征，我们称之为混合型任务。有时候我们可能需要将这种呢混合型任务进一步分解为CPU密集型和I/O密集型这两种子任务，并使用专门的工作者线程来负责这些子任务的执行，以提高并发性。

使用多线程编程的一个好的方式是从单线程程序开始，只有在多线程程序算法本身没有最大性能瓶颈但仍然无法满足要求的情况下我们才考虑使用多线程。

### 按处理步骤分隔

按任务的资源属性分割可以被看作是按处理步骤分割的一个特例。多线程设计模式中的 pipeline 模式的核心思想也正是按处理步骤分隔。

类似于按任务的资源消耗属性分割，在按处理步骤分割中，工作者线程之间传递数据同样需要借助线程安全的队列，而这也会增加相应的开销。即，按处理步骤分割可能导致单个输入元素的处理时间相对变大，即延时增加。

同样，在按处理步骤分割中，我们也需要注意工作者线程数的合力设置：工作者线程数过多可能会导致过多的上下文切换，这反而降低了程序的吞吐率。因此，保守的设置方法是从仅为每个处理步骤设置一个工作者线程开始，在确实有证据显示有必要增加某个处理步骤的工作者线程数的情况下才增加线程数。

## 合理设置线程数

线程数不宜过小，过小可能导致无法充分利用处理器资源；线程数也不宜过大，过大会增加上下文切换以及其他开销。那么，我们如何设置一个合理的线程数呢？在回答这个问题前，我们先看一下线程数与多线程程序相对于单线程程序的提速(Speedup)之间的关系。

### Amdahl's 定律

Amdahl's 定律描述了线程数与多线程程序相对于单线程程序的提速之间的关系。设处理器的数量为 N，程序中必须串行的部分耗时占全部耗时的比例为P，那么将这样一个程序改为多线程程序，我们能够获得的理论上最大提速 $S_{max}$ 与 N、P 之间的关系就是 Amdahl's 定律内容：$S_{max}=1/(P+(1-P)/N)$ 。

从上述推导过程可以看出，多线程程序的提速主要来自于多个线程对程序中可并行化部分的耗时均摊。当 $N\to\infty$ 时，$S_{max}\to\frac{1}{P}$。

最终决定多线程程序提速的因素是整个计算中串行部分的耗时比率P，而不是线程数N。因此，为了使多线程程序nenggou好的最大的提速，我们应该从算法入手，减少程序中必须串行的部分，而不是寄希望于增加线程数(或处理器的数目)。

### 线程数设置的原则

线程数设置过少可能导致无法充分利用处理器资源；而线程数设置过大可能导致过多的上下文切换，从而反倒降低了系统的性能。然而，设置一个既不过大，也不过小的绝对理想的线程实际上是不可能的。这是因为设置一个绝对理想的线程数所需的信息对我们来说总是不充分的。设置一个合理的线程数实际上就是避免随意设置线程，即在设置线程数时，尽可能地考虑一些可以实际操作的因素，这些元素包括系统的资源状况(处理器数目、内存容量等)、线程所执行的任务特性(CPU密集型、I/O密集型)、资源使用情况规划(CPU使用率上限)以及程序运行过程中使用到的其他稀缺资源的情况(如数据库连接、文件句柄数)等。

设 $N_{cpu}$ 表示一个系统的处理器数目，$N_{cpu}$ 的具体值可以通过 `Runtime.getRuntime().availableProcessors()` 获取。

线程数合理值可以根据以下规则设置：
- 对于CPU密集型线程，考虑到这类线程执行任务时消耗的主要是CPU资源，我们可以将这类线程的线程数设置为$N_{cpu}$个。因为CPU密集型线程也可能由于某种原因(比如说缺页中断/Page Fault)而被切出，此时，为了避免处理器资源的浪费，我们可以为这类线程设置一个额外的线程，即将线程数设置为$N_{cpu}$+1。
- 对于 I/O 密集型线程，考虑到I/O操作可能导致上下文切换，为这样的线程设置过多的线程数会导致过多的额外系统开销。因此如果一个这样的工作者线程就足以满足我们的要求，那么就不要设置更多的线程数。如果一个工作者线程仍然不够，那么我们可以考虑将这类线程的数量设置为2×$N_{cpu}$，这是因为 I/O 密集型线程在等待 I/O 操作返回结果时是不占用处理器资源的，因此我们可以为每个处理器安排一个额外的线程以提高处理器资源的利用率。



