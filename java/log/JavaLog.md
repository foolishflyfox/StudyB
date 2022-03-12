# 日志类库详解

## 日志库简介

- 最重要的一点是：区分日志系统和日志门面；
- 其次是日志库的使用，包括配置与 API 使用；配置侧重于日志系统的配置，API 使用侧重于日志门面；
- 最后是选型，改造和最佳实践；

## 日志库之日志系统

### java.util.logging (JUL)

JDK1.4 开始，通过 java.util.logging 提供的日志功能。虽然是官方自带的 log lib，JUL 的使用并不广泛。主要原因是：

- JUL 从 JDK1.4 才开始加入(2002年)，当时各种第三方 log lib 已经被广泛应用了；
- JUL 早期存在性能问题，到 JDK1.5 才有不错的进步，但现在和 Logback / Log4j2 相比还是欠佳；
- JUL 的功能不如 Logback / Log4j2 等完善，比如 Output Handler 就没有它们丰富，有时候需要自己来继承定制，又比如默认没有从 ClassPath 里加载配置文件的功能；

## 日志库之日志门面

Slf4j 是对不同日志框架提供的一个 API 封装，可以在部署的时候，不修改任何配置即可接入一种日志实现方案。但是，slf4j 在编译器静态绑定真正的 Log 库。使用 SLF4J 时，如果你需要使用某一种日志实现，那么你必须选择正确的 SLF4J 的 jar 包的集合(各种桥接包)。

![](https://www.slf4j.org/images/concrete-bindings.png)

### slf4j 绑定日志

在通过 `compile "ch.qos.logback:logback-classic:1.0.13"` 引入 logback-classic-1.0.13.jar ，会自动将  ch.qos.logback:logback-core、 org.slf4j:slf4j-api 也添加你的项目中。

### log4j2 绑定日志

通过 `compile "org.apache.logging.log4j:log4j-slf4j-impl:2.9.1"` 即可，依赖关系为：
```
\--- org.apache.logging.log4j:log4j-slf4j-impl:2.9.1
     +--- org.slf4j:slf4j-api:1.7.25
     +--- org.apache.logging.log4j:log4j-api:2.9.1
     \--- org.apache.logging.log4j:log4j-core:2.9.1
          \--- org.apache.logging.log4j:log4j-api:2.9.1
```

### log4j2 基本配置如下

参考：https://www.cnblogs.com/hafiz/p/6170702.html

#### 配置文件的名称以及在项目中的存放位置

log4j 2.x 版本不再支持像 1.x 中的 .properties 后缀的文件配置方式，2.x 版本配置文件的后缀只能为 .xml / .json 或 .jsn。

系统选择配置文件的优先级从高到底分别为：
1. classpath 下的名为 log4j2-test.json 或者 log4j2-test.jsn 的文件；
2. classpath 下的名为 log4j2-test.xml 的文件；
3. classpath 下的名为 log4j2.json 或 log4j2.jsn 的文件；
4. classpath 下的名为 log4j2.xml 的文件；

我们一般默认使用 log4j2.xml 进行命名。如果本地要测试，可以把 log4j2-test.xml 放到 classpath，而正式环境使用 log4j2.xml 则在打包部署的时候不要打包 log4j2-test.xml 即可。

在 gradle 中通过修改 sourceSets 中 resources 可以修改 log4j2.xml 的路径。

#### 缺省默认配置文件

```xml
<?xml version="1.0" encoding="UTF-8"?>
<Configuration status="WARN">
  <Appenders>
    <Console name="Console" target="SYSTEM_OUT">
      <PatternLayout pattern="%d{HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n"/>
    </Console>
  </Appenders>
  <Loggers>
    <Root level="error">
      <AppenderRef ref="Console"/>
    </Root>
  </Loggers>
</Configuration>
```

#### 配置文件节点解析

根节点 Configuration 有两个属性，status 和 monitorinterval。有两个子节点 Appenders 和 Loggers。

status 用来指定 log4j 本身的打印日志的级别。monitorinterval 用来指定 log4j 自动重新配置的监测间隔时间，单位是s，最小是5s。

Appenders 节点通常有3种子节点：Console、RollingFile、File。

Console 节点用来定义输出到控制台的 Appender。name 指定 Appender 的名字。target 可以为 SYSTEM_OUT 或 SYSTEM_ERR，一般只设置默认 SYSTEM_OUT。PatternLayout 输出格式，不设置默认为 `%m%n`。

File 节点用来定义输出到指定位置的文件的 Appender。name 指定 Appender 的名字，fileName 指定输出日志的目的文件带全路径的文件名。PatternLayout 输出格式，不设置默认为 `%m%n`。

RollingFile 节点用来定义超过指定大小自动删除旧的文件，创建新文件的 Appender。name 指定 Appender 的名字，fileName 指定输出日志的目的文件带全路径的文件名，PatternLayout 指定输出格式，不设置默认为 `%m%n`，filePattern 指定新建日志文件的名称格式，Policies 指定滚动日志的策略，即什么时候新建日志文件。

Logger 节点常见的有两种：Root 和 Logger。

Root 节点用来指定项目的根日志，如果没有单独指定 Logger，那就默认使用 Root 日志输出。

level 日志输出级别，有 8 个级别，从低到高为：All < Trace < Debug < Info < Warn < Error < Fatal < OFF。

#### PatternLayout 的配置

- `${hostName}`: 主机名
- `%d{yyyy-MM-dd HH:mm:ss.SSS}`: 打印时间
- `%p`: 日志等级，如 INFO、WARN 等
- `%t` / `%thread`: 线程名
- `%pid`: 进程id
- `%c`: 日志名称


