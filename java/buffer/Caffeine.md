# Caffine

## 缓存填充策略

缓存的填充方式有 3 种：手动、同步与异步；

### 手动加载

手动控制缓存的增删改处理，主动增加、获取以及依据函数式更新缓存，底层使用 ConcurrentHashMap 进行节点存储，因此 get 方法是安全的。批量查找可以使用 getAllPresent() 方法或者带填充默认值的 getAll() 方法。

- expireAfterWrite: 表示指定时间后，缓存中 key 对应的 value 失效，设置为 null，加载函数被重新调用；
- refreshAfterWrite：表示指定时间后，缓存中 key 对应的 value 要被重新异步载入，但实际情况是，在指定时间后，并不会触发异步载入动作，而是调用 get 时检查值是否过期，如果已经过期，就启动异步载入，并且返回的是旧值。因此，在吞吐量很低的情况下，如果很长一段时间内没有查询之后，发生的查询有可能会得到一个旧值(这个旧值可能来自于很长时间之前)，这将引发问题。

一般来说 refreshAfterWrite 会设置得时间短一点，对于高频访问，能够及时更新缓存。再设置一个 expireAfterWrite，时间会长一点，对于低频访问，如果expire和refresh条件同时满足，优先同步获取数据，能够避免在很长时间后，访问到的是很早之前旧的数据。

Caffeine 的一个目的是减少访问加载函数，因此称为一个内存缓存器。
