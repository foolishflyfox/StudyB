# Java API

## java.util

### concurrent

并行相关的库。

- CountDownLatch: 用于判断所有条件是否都已经满足，await 的调用线程被称为等待线程，其他线程通过 countDown 声明一个条件已经完成，当所有条件都完成后，等待线程继续执行；
- CyclicBarrier：同步多个相关线程一起执行，多个相关线程调用 await 方法，被阻塞，知道调用 await 方法的线程数达到指定格式，所有相关的等待线程继续执行；
- Semaphore: 指定可以同时执行的线程数量，线程通过 acquire 获取执行权限，如果已经达到上限，则被阻塞，在释放相关资源后，需要调用 release 释放执行权限，调用 release 前不一定需要 acquire 权限，这使得 Semaphore 可以模拟Condition，也可以模拟 Lock，当然不建议通过 Semaphore 模拟它们。
#### locks

存放锁。

- Condition：用于线程间同步；


### HashMap



### IdentityHashMap
