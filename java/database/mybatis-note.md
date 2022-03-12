# 集成 MyBatis

使用 Hibernate 或 JPA 操作数据库时，这类 ORM 干的主要工作就是把 ResultSet 的每一行变为 Java Bean，或者把 Java Bean 自动转换扫 INSERT 或 UPDATE 语句的参数中，从而实现 ORM。

而 ORM 框架之所以知道如何把行数据映射到 Java Bean，是因为我们在 Java Bean 的属性上给了足够的注解作为元数据，ORM 框架获取 Java Bean 注解后，就知道如何进行双向映射了。

那么，ORM 框架是如何跟踪 Java Bean 的修改，以便在 update() 操作中更新必要的属性？答案是使用 Proxy 模式，从 ORM 框架读取的 User 实例实际上并不是 User 类，而是代理类，代理类继承自 User 类，但对每隔 setter 方法就行了复写。

