# Hello world 探究

```java
@SpringBootApplication  // 告诉 springboot ，这是一个 springboot 应用
public class MainApp {
    public static void main(String[] args) {
        // 固定写法，让 springboot 跑起来
        ApplicationContext context = SpringApplication.run(MainApp.class, args);
        MainApp bean = context.getBean(MainApp.class);
        System.out.println(bean);
    }
}
```
@SpringBootApplication，Spring Boot 应用标注在某个类上，说明这个类是 Spring Boot 的主配置类。Spring Boot 就应该运行这个类的 main 方法来启动 Spring Boot 应用。

Spring Boot 是组合注解：
```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(excludeFilters = {
		@Filter(type = FilterType.CUSTOM, classes = TypeExcludeFilter.class),
		@Filter(type = FilterType.CUSTOM, classes = AutoConfigurationExcludeFilter.class) })
public @interface SpringBootApplication {

	@AliasFor(annotation = EnableAutoConfiguration.class, attribute = "exclude")
	Class<?>[] exclude() default {};

	@AliasFor(annotation = EnableAutoConfiguration.class, attribute = "excludeName")
	String[] excludeName() default {};

	@AliasFor(annotation = ComponentScan.class, attribute = "basePackages")
	String[] scanBasePackages() default {};

	@AliasFor(annotation = ComponentScan.class, attribute = "basePackageClasses")
	Class<?>[] scanBasePackageClasses() default {};
}
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Configuration
public @interface SpringBootConfiguration {

}

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Configuration {

	String value() default "";

}
```
- @SpringBootConfiguration: SpringBoot 的配置类，标注在某个类上，表示这是一个 SpringBoot 的配置类。
    - @Configuration 配置类上标注这个注解，配置类也是容器中的一个组件 @Component
- @EnableAutoConfiguration 开启自动配置功能，以前我们需要配置的东西，Spring Boot 帮我们自动配置。
```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@AutoConfigurationPackage
@Import(EnableAutoConfigurationImportSelector.class)
public @interface EnableAutoConfiguration {
    Class<?>[] exclude() default {};

	String[] excludeName() default {};
}
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(AutoConfigurationPackages.Registrar.class)
public @interface AutoConfigurationPackage {

}
```
AutoConfigurationPackages.Registrar.class: 将主配置类（@SpringBootApplication标注的类）所在包及下面所有子包里面的所有组件扫描到 Spring 容器中。

EnableAutoConfigurationImportSelector: 导入哪些组件的选择器。将所有需要导入的组件以全类名的方式返回；这些组件就会被添加到容器中。会给容器中导入非常多的自动配置类（xxxAutoConfiguration），就是给容器中导入这个场景所需要的所有组件，并配置号这些组件。
```
org.springframework.boot.autoconfigure.admin.SpringApplicationAdminJmxAutoConfiguration
org.springframework.boot.autoconfigure.aop.AopAutoConfiguration
org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration
org.springframework.boot.autoconfigure.batch.BatchAutoConfiguration
org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration
org.springframework.boot.autoconfigure.cassandra.CassandraAutoConfiguration
org.springframework.boot.autoconfigure.cloud.CloudAutoConfiguration
org.springframework.boot.autoconfigure.context.ConfigurationPropertiesAutoConfiguration
org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration
org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration
org.springframework.boot.autoconfigure.couchbase.CouchbaseAutoConfiguration
org.springframework.boot.autoconfigure.dao.PersistenceExceptionTranslationAutoConfiguration
org.springframework.boot.autoconfigure.data.cassandra.CassandraDataAutoConfiguration
org.springframework.boot.autoconfigure.data.cassandra.CassandraRepositoriesAutoConfiguration
... ...
```
有了自动配置类，免去了我们手动编写配置，注入功能组件等的工作。
SpringFactoriesLoader.loadFactoryNames(EnableAutoConfiguration.class, this.beanClassLoader)
Spring Boot 在启动的时候，从类路径下："META-INF/spring.factories" 中获取 EnableAutoConfiguration 指定的值，将这些值作为自动配置类导入到容器中，自动配置类就生效了，帮我们自动配置工作。以前我们需要自己配置的东西，自动配置类都帮我们做了。

J2EE 的主体解决方案和自动配置都在 spring-boot-autoconfigure-1.5.15.RELEASE.jar 中进行。

## 配置文件

SpringBoot 使用全局的配置文件，配置文件名是固定的：application.properties、application.yaml。配置文件的作用，修改 SpringBoot 自动配置的默认值，SpringBoot 在底层都给我们自动配置好。

YAML（Yet Another Markup Language）。YAML 以数据为中心，比 XML、JSON 更适合做配置。

YAML 的基本语法：
- k: v 一个键值对，必须有空格；以空格的缩减表示层级关系，重要左对齐的一列数据，都是同一层级的。属性和值是大小写敏感的。
- 值的写法
    - 字面量：普通的值（数字、字符串、布尔）；字符串可以不加上单引号或双引号；也可以带上引号；
        - 双引号不会转义字符串里面的特殊字符，例如 "zhangsan \n lisi"，输出 `zhangsan 换行 lisi`；单引号换行输出为 `\n`;
    - 对象：Map（属性和值、键值对）；对象还是 k:v 格式，在下一行写对象的属性和值，注意缩减；也可以是行内写法：
        - `friends: {lastname: zhangsan, age: 18}`
    - 数组: 用 `- 值` 表示数组中的一个元素；行内写法：`pets: [cat,dog,pig]`

@Value 获取值和 @ConfigurationProperties 获取值比较：

# Spring Boot 与日志

市面上的日志框架：JUL、JCL、JBoss-logging、logback、log4j、log4j2、slf4j ...

日志门面：JCL(Jakarta Commons Logging)、SLF4j(Simple Logging Facade for Java)、JBoss-logging
日志实现：Log4j、JUL、log4j2、Logback


