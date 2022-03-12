# Slf4j + log4j

## 引入库

需要引入以下的库：
```java
dependencies {
    compile "org.apache.logging.log4j:log4j-core:$log4jVersion"
    compile "org.apache.logging.log4j:log4j-slf4j-impl:$log4jVersion"
    compile "org.projectlombok:lombok:$lombokVersion"
    compile "com.fasterxml.jackson.core:jackson-core:$jacksonVersion"
    testCompile "junit:junit:$jUnitVersion"
}
```

## 定义日志格式

日志格式定义在 resources 的 log4j2.xml 文件中：
```xml
<?xml version="1.0" encoding="UTF-8"?>
<!--
    status : 这个用于设置log4j2自身内部的信息输出,可以不设置,当设置成trace时,会看到log4j2内部各种详细输出。
    因此我们直接设置成OFF
 -->
<Configuration status="OFF">
    <!-- 配置输出端  -->
    <Appenders>
        <!-- 输出到控制台  -->
        <Console name="Console" target="SYSTEM_OUT">
            <PatternLayout pattern="[%-level]%d{yyyy-MM-dd HH:mm:ss.SSS} [%t] %logger{36} - %msg%n"/>
        </Console>
    </Appenders>
    <!-- 配置Loggers  -->
    <Loggers>
        <!--
            Logger： 用于指定部分包的日志级别
                日志级别局部的会覆盖全局的
                比如这里hibernate的级别设为debug，而控制台没有设级别，那么控制台会打印debug级别的日志
                而输出到文件这个输出端设置了info级别，那么hibernate的debug级别的日志还是看不了。
                所以最终输出的级别和输出端设置的级别是有关系的。
            name: 包名
            level：日志级别
            additivity：是否冒泡，既在当前logger的输出端输出日志后
                             是否需要在父输出端上输出该日志，默认为 true。
                             如果设为false，则必须配置AppendRef。
         -->
        <Logger name="org.hibernate" level="debug" additivity="true"/>

        <!-- 这个root是配置全局日志级别和输出端功能和老版的log4j中根的配置是一样的 -->
        <Root level="info">
            <!-- 这里引用上面定义的输出端，千万不要漏了。 -->
            <AppenderRef ref="Console"/>
            <!--<AppenderRef ref="RollingFileInfo"/>-->
        </Root>
    </Loggers>
</Configuration>
```
重点关注 PatternLayout  (可参考 http://logging.apache.org/log4j/2.x/manual/lookups.html#ContextMapLookup )：

- `%level`：日志等级，等价于 `%p`，如 INFO、WARN 等；
- `%d`：日期，默认格式如 `2021-10-28 17:03:23,558`，可以指定格式，如 `%d{yyyy-MM-dd HH:mm}` 输出为 `2021-10-28 17:05.17.268`；
- `%t`: 线程名；
- `%c`: 调用 log 函数输出的类名，可以指定路径上最多显示的字符数，例如正常显示为 `com.bfh.T1`，在 `%c{1.}` 下显示为 `c.b.T1`，在 `%c{2.}` 下显示为 `co.bf.T1`；
- `%m`: 需要打印的内容；
- `%X{变量名}`: 获取存储在 `org.apache.logging.log4j.ThreadContext` 中的变量的值，例如 `%X{apple}`，在代码中指定 `apple` 的值 `ThreadContext.put("apple", "12345");`，则输出为 `12345`；
- `${hostName}`: 主机名，如 `XXXMacBook-Pro.local`;

