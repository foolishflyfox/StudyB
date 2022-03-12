# Spring 简介

SSM：SpringMVC / Spring / MyBatis
SSH: Structs / Spring / Hibernate

框架：高度抽取可重用代码的一种设计：高度的通用性。多个可重用模块的集合，形成一个某个领域的整体解决方案。

Spring 是一个 Java 容器框架，可以管理所有的组件(类)，核心是 IoC 和 AOP。

Spring 的优良特性：
- 非侵入式；
- 依赖注入；
- 面向切面编程；
- 容器：Spring 是一个容器，因为它包含并且管理应用对象的生命周期；
- 组件化：Spring 实现了使用简单的组件配置组合成一个复杂的应用。在 Spring 中可以使用 XML 和 java 注解组合这些对象；
- 一站式：在 IoC 和 AOP 基础上可以整合各种企业应用的开源框架和优秀的第三方类；


IoC 提供容器功能，通过这个容器可以整合其他功能，如 Struts2、Hibernate、MyBatis。

AOP：面向切面编程，主要为了学习声明式事务。

## IoC

Inversion of Control，控制反转。控制是指资源的获取方式，资源的获取方式有两种，主动式和被动式。主动式表示要什么资源都自己创建即可，对于简单对象而言，主动式资源获取比较方便，但是对于复杂对象就比较麻烦。被动式表示资源的获取不是程序员自己创建，而是交个一个容器来创建和设置。

容器：管理所有的组件(有功能的类)。容器可以自动探查出哪些组件需要用到另一些组件，会自动创建被依赖对象并赋值过去。由主动地 new 资源变为被动地接收资源。

DI：Dependency Injection，依赖注入，IoC 是一种思想，DI 是 IoC 的一种实现方式。容器知道组件运行的时候需要另一个组件，容器通过反射的形式，将容器中准备好的被依赖对象注入(利用反射给属性赋值)到组件中。

## 写 Spring 的项目

1. 导包
2. 写配置，IDEA 通过 New -> XML Configuration File -> Spring Config 创建。
3. 测试

### hello world

hello-world 项目源码位于 code/bilibili-spring/hello-world 目录下。

- 创建一个 gradle 项目；
- 修改 build.gradle 文件，导入 Spring 相关的包
```groove
dependencies {
    testCompile group: 'junit', name: 'junit', version: '4.12'
    compile 'org.projectlombok:lombok:1.18.22'

    // 导入依赖的4个包，spring-bean / spring-context/ spring-core / spring-expression
    // 导入spring-context，其他3个包被自动导入
    compile 'org.springframework:spring-context:4.0.0.RELEASE'
}
```
- 创建一个被需要被 IoC 容器管理的类，Person
```java
package com.bfh;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Person {
    private String lastName;
    private Integer age;
    private String gender;
    private String email;
}
```
- 创建 Spring 配置文件 ioc.xml (文件名可以修改)，该 xml 文件需要位于 src/main/java 中(或者其 resources 目录下)，并创建一个 Person 对象；
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

    <!-- 注册一个 Person 对象，Spring 会自动创建这个 Person 对象  -->
    <!--  通过 ctrl+n 可自动生成  -->
    <!--  一个 Bean 标签可以注册一个组件(对象，类)  -->
    <bean id="person01" class="com.bfh.Person">
        <property name="lastName" value="张三"/>
        <property name="age" value="18"/>
        <property name="gender" value="男"/>
        <property name="email" value="zhangsan@abc.com"/>
    </bean>
    
</beans>
```
- 测试，编写测试代码
```java
package com.bfh;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class IocTest {

    /**
     * 从容器中拿到组件
     */
    @Test
    public void test() {
        // ApplicationContext，代表 ioc 容器
        // 当前应用的 xml 配置文件在 ClassPath 下
        ApplicationContext ioc = new ClassPathXmlApplicationContext("ioc.xml");
        Person person01 = (Person) ioc.getBean("person01");
        System.out.println(person01);
    }
}
```
创建的容器类型为 `ClassPathXmlApplicationContext`，表示在当前 Classpath 下搜索 spring 配置文件 ioc.xml。通过 `System.getProperty("java.class.path")` 可以获取 Classpath 的目录文件夹。
```
/Users/fenghuabin/Code/GitRepositories/StudyNote/code/blibili-spring/hello-world/build/classes/java/test
/Users/fenghuabin/Code/GitRepositories/StudyNote/code/blibili-spring/hello-world/build/resources/test
/Users/fenghuabin/Code/GitRepositories/StudyNote/code/blibili-spring/hello-world/build/classes/java/main
/Users/fenghuabin/Code/GitRepositories/StudyNote/code/blibili-spring/hello-world/build/resources/main
/Users/fenghuabin/.gradle/caches/modules-2/files-2.1/org.projectlombok/lombok/1.18.22/9c08ea24c6eb714e2d6170e8122c069a0ba9aacf/lombok-1.18.22.jar
/Users/fenghuabin/.gradle/caches/modules-2/files-2.1/org.springframework/spring-context/4.0.0.RELEASE/57586271e775982f8961e1e7e338447d4af1864c/spring-context-4.0.0.RELEASE.jar
/Users/fenghuabin/.m2/repository/junit/junit/4.12/junit-4.12.jar
/Users/fenghuabin/.gradle/caches/modules-2/files-2.1/org.springframework/spring-aop/4.0.0.RELEASE/cceb3e9510774b3f88b008a70c6a9aefb98d891f/spring-aop-4.0.0.RELEASE.jar
/Users/fenghuabin/.gradle/caches/modules-2/files-2.1/org.springframework/spring-beans/4.0.0.RELEASE/97e97a04aede419679392176ec1df175d3e9a7a0/spring-beans-4.0.0.RELEASE.jar
/Users/fenghuabin/.gradle/caches/modules-2/files-2.1/org.springframework/spring-expression/4.0.0.RELEASE/199be03c976524b6427dff31078227bfc703400b/spring-expression-4.0.0.RELEASE.jar
/Users/fenghuabin/.gradle/caches/modules-2/files-2.1/org.springframework/spring-core/4.0.0.RELEASE/73b485e25b13a7a44b73a301fe6757c3cbad453e/spring-core-4.0.0.RELEASE.jar
/Users/fenghuabin/.m2/repository/org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar
/Users/fenghuabin/.m2/repository/aopalliance/aopalliance/1.0/aopalliance-1.0.jar
/Users/fenghuabin/.gradle/caches/modules-2/files-2.1/commons-logging/commons-logging/1.1.1/5043bfebc3db072ed80fbd362e7caf00e885d8ae/commons-logging-1.1.1.jar
```
可以看到，第4行会搜索项目的 build/resources/main 文件夹，而构建完成后，会有一个 ioc.xml 的副本在该目录下。
```
$ find . -name ioc.xml
./build/resources/main/ioc.xml
./src/main/resources/ioc.xml
```
因此，配置文件可以被找到。执行结果为：
```
Person(lastName=张三, age=18, gender=男, email=zhangsan@abc.com)
```
说明 Person 对象已经创建，并且被 IoC 容器管理。

FileSystemXmlApplicationContext 通过磁盘路径制定 Spring 配置文件。

容器中对象的创建，在容器创建的时候就完成了。容器中如果没有对应的组件，那么通过 `getBean` 获取组件时将抛出：`NoSuchBeanDefinitionException` 异常。

ioc 容器在创建组建对象的时候，property 会利用 setter 方法为组件赋值的。

### 实验二 根据bean类型获取对象

`<T> T ApplicationContext::getBean(Class<T> requiredType)`。如果有多个相同类型的组件，通过该类型找对应组件时，将报错：`NoUniqueBeanDefinitionException`。通过 `<T> T getBean(String name, Class<T> requiredType)` 可以解决上述问题。

### 实验三 通过构造器创建 bean

对应的 spring 配置为：
```
    <bean id="person3" class="com.bfh.Person">
        <constructor-arg name="lastName" value="小明"></constructor-arg>
        <constructor-arg name="gender" value="男"></constructor-arg>
        <constructor-arg name="email" value="xiaoming@abc.com"></constructor-arg>
        <constructor-arg name="age" value="12"></constructor-arg>
    </bean>
```
还有一种依赖于参数顺序的有参构造器：
```
    <bean id="person4" class="com.bfh.Person">
        <constructor-arg value="小花"></constructor-arg>
        <constructor-arg value="18"></constructor-arg>
        <constructor-arg value="女"></constructor-arg>
        <constructor-arg value="xiaohua@abc.com"></constructor-arg>
    </bean>
```

### 实验四 通过 p 名称空间为 bean 赋值

名称空间：在 xml 中，名称空间是为了防止标签重复的。

修改 beans 属性，添加 p 空间 `xmlns:p="http://www.springframework.org/schema/p"`：
```xml
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:p="http://www.springframework.org/schema/p"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">
</beans>
```
在定义组件时可以使用 p 空间：
```xml
    <bean id="person6" class="com.bfh.Person"
          p:age="18" p:email="abc@xxx.com" p:lastName="abc">
    </bean>
```
p 空间只是为了简化 xml 的编写。

### 正确为各种属性赋值

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean id="car" class="com.bfh.Car">
        <property name="name" value="宝马"/>
        <property name="price" value="10000"/>
    </bean>

    <bean id="person" class="com.bfh.Person">
        <property name="car" ref="car"/>
        <property name="lastName" value="张三"/>
    </bean>

    <bean id="person2" class="com.bfh.Person">
        <property name="car">
            <bean class="com.bfh.Car">
                <property name="name" value="奔驰"/>
                <property name="price" value="8888"/>
            </bean>
        </property>
    </bean>
</beans>
```

为 List 对象赋值：
```xml
    <bean id="book001" class="com.bfh.Book">
        <property name="name" value="三国演义"/>
        <property name="author" value="罗贯中"/>
    </bean>
    <bean id="person3" class="com.bfh.Person">
        <!-- 为list类型赋值 -->
        <property name="books">
            <!-- books = new ArrayList<Book>() -->
            <list>
                <!-- 即使加了 id，也不能通过 getBean 获取，内部变量只能内部使用 -->
                <bean id="innerBook" class="com.bfh.Book">
                    <property name="name" value="西游记"/>
                    <property name="author" value="吴承恩"/>
                </bean>
                <ref bean="book001"/>
            </list>
        </property>
    </bean>
```
为 map 赋值：
在 Person 中定义 Map 类型 `private Map<String, Object> maps;`，配置中定义为：
```xml
    <bean id="person4" class="com.bfh.Person">
        <property name="maps">
            <map>
                <entry key="a" value="18"/>
                <entry key="b" value="abc"/>
                <entry key="book" value-ref="book001"/>
                <entry key="car">
                    <bean class="com.bfh.Car">
                        <property name="name" value="特斯拉"/>
                    </bean>
                </entry>
            </map>
        </property>
    </bean>
```
生成的 map 为 java.util.LinkedHashMap 类型。

为 Properties 对象赋值，在 Person 中定义类型 `private Properties properties`
```xml
    <bean id="person5" class="com.bfh.Person">
        <property name="properties">
            <props>
                <!-- 所有的 key value 都是 String -->
                <prop key="username">root</prop>
                <prop key="password">123456</prop>
            </props>
        </property>
    </bean>
```

通过 util 命名空间创建全局map对象，spring 配置文件头添加 `xmlns:util="http://www.springframework.org/schema/util"`，并在 xsi:schemaLocation 添加 http://www.springframework.org/schema/util http://www.springframework.org/schema/util/spring-util-4.0.xsd ：
```xml
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:util="http://www.springframework.org/schema/util"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/util
        http://www.springframework.org/schema/util/spring-util-4.0.xsd">
```
指定全局 map：
```xml
    <util:map id="myMap">
        <entry key="aaa" value="张三"></entry>
        <entry key="bbb" value="李四"/>
        <entry key="ccc">
            <value type="double">12.34</value>
        </entry>
    </util:map>
```
执行
```java
    Map<String, Object> myMap = (Map<String, Object>)ioc2.getBean("myMap");
    System.out.println(myMap);
    System.out.println(myMap.get("ccc").getClass().getName());
```
结果为：
```
{aaa=张三, bbb=李四, ccc=12.34}
java.lang.Double
```

级联属性：属性的属性。
```xml
    <bean id="person7" class="com.bfh.Person">
        <property name="car" ref="car"/>
        <property name="car.price" value="1234567"/>
    </bean>
```
级联属性可能改变引用的值。

指定 bean 的 parent，指明配置继承：
```xml
    <bean id="person6" class="com.bfh.Person">
        <property name="lastName" value="张三"/>
        <property name="age" value="18"/>
        <property name="gender" value="男"/>
        <property name="email" value="zhangsan@abc.com"/>
    </bean>
    <bean id="person7" class="com.bfh.Person" parent="person6">
        <property name="lastName" value="李四"/>
        <property name="email" value="lisi@abc.com"/>
        <property name="maps" ref="myMap"/>
    </bean>
```
代码：
```java
ApplicationContext context = new ClassPathXmlApplicationContext("ioc2.xml");
Person person7 = context.getBean("person7", Person.class);
System.out.println(person7);
```
执行结果为：
```
Person(lastName=李四, age=18, gender=男, email=lisi@abc.com, car=null, books=null, maps={aaa=张三, bbb=李四, ccc=12.34}, properties=null)
```

bean 添加 `abstract="true"`，表示该 bean 只能被继承，不能被获取。

静态工厂的 spring xml 配置：
```xml
    <bean id="airPlane01" class="com.bfh.factory.AirPlaneStaticFactory" factory-method="getAirPlane">
        <constructor-arg name="captainName" value="李四"/>
    </bean>
```

实例工厂的 spring xml 配置：
```xml
<!-- 实例工厂方法 -->
    <bean id="airPlaneInstanceFactory" class="com.bfh.factory.AirPlaneInstanceFactory">
    </bean>

    <bean id="airPlane02" class="com.bfh.AirPlane" factory-bean="airPlaneInstanceFactory" factory-method="getAirPlan">
        <constructor-arg name="captainName" value="王五"/>
    </bean>
```

- Service / Repository / Controller / Componnent 4 种注释在机器看来等价，对程序员有意义。

- @Autowired
    - 先安装类型去容器中找对应的组件
        1. 找到一个，赋值
        2. 没有找到，抛异常
        3. 知道多个
            3.1 按变量名继续匹配
                a. 匹配上；
                b. 没有匹配上：抛异常；可以通过 `@Qualifier("bookDaoExtend")` 指定 id；

Autowired 标注的自动装配必须找到对应的 bean。通过 `@Autowired(required = false)` 允许找不到。

方法上有 `@Autowired`，这个方法会在 bean 创建的时候运行，该方法上的每一个参数都会自动注入值。

@Autowired 强大，是 spring 自己的、@Resource 是 Java 的，扩展性更强，如果切换成另外的容器框架，Resource 还是可以使用，Autowired 就不行。不过 spring 一家独大，不会且容器框架。


