# Spring 

## Spring 概述

Spring 是最受欢迎的企业级 Java 应用程序开发框架，是一个开源的 Java 平台。Spring 是轻量级的，Spring 框架的基础版本是在 2MB 左右。Spring 框架核心特性可用于开发任何 Java 应用程序，但是在 Java EE 平台上构建 Web 应用程序需要扩展。Spring 框架的目标是使 J2EE 开发变得更容易使用，通过启用基于 POJO 编程模型来促进更好的编程实践。

### 核心思想

#### IoC

IoC，即 Inversion of Control，意为控制反转。Spring 最认同的技术使控制反转的 **依赖注入(DI)** 模式。控制反转是一个通用的概念，它可以用许多不同的方式去表达，依赖注入仅仅是控制反转的一个具体的例子。

当编写一个复杂的 Java 应用程序时，应用程序类应该尽可能地独立于其他的 Java 类来增加这些类的可重用性，当进行单元测试时，可以使它们独立于其他类进行测试。依赖注入（有时候也被称为配线）有助于将这些类粘合在一起，并且在同一时间让它们保持独立。

到底什么是依赖注入？让我们将这两个词分开看一看。这里将依赖关系部分转换为两个类之间的关联。例如，类 A 依赖于类 B。现在让我们看一看第二部分，注入。所有这一切意味着类 B 将通过 IoC 被注入到类 A 中。

依赖注入可以向构造函数传递参数的方式发生，或者通过使用 setter 方法 pos-construction。依赖注入是 Spring 框架的核心。

#### AOP

Spring 框架的一个关键组件是面向切面的程序设计(AOP)，一个程序中跨越多个点的功能被称为切面。这些切面在概念上独立于应用程序的业务逻辑。有各种各样常见的很好的关于切面的例子：比如日志记录、声明性事务、安全、缓存等。

在 OOP 中模块化的关键单元是类，而在 AOP 中，模块化的关键单元是切面。

AOP 帮助你将横切关注点从它们锁影响的对象中分离出来，依赖注入帮助你将应用程序对象从彼此依赖中分离出来。

Spring 框架的 AOP 模块提供了面向切面的程序设计实现，允许你定义拦截器方法和切入点，可以实现将应该被分开的代码干净地分开功能。

### Spring 体系结构

Spring 当前框架有 20 个 jar 包，大致可以分为 6 个模块：

- Data Access / Integration
    - JDBC：spring-jdbc，提供了一个 JDBC 抽象层；
    - ORM：spring-orm，提供了流行的对象关系型映射 API 集，如 JPA、JDO、Hibernate；
    - OXM：spring-oxm，提供了一个抽象层以支持对象/XML映射的实现，如 JAXB、Castor、XMLBeans、JiBX 和 XStream；
    - JMS：spring-jms，包含了生产和消费消息的功能；
    - Transaction：spring-tx 支持编程和声明式事务管理类；
- Web
    - WebSocket
    - Servlet
    - Web：spring-web，提供了基本的面向 web 的功能，如多文件上传、使用 Servlet 监听器的 IoC 容器的初始化，一个面向 web 的应用层上下文；spring-webmvc，包括 MVC 和 REST web 服务实现；
    - Portlet：spring-webmvc-portlet，提供在 Portlet 环境的 MVC 实现和 spring-webmvc 功能的镜像；
- Test
    - spring-test：以 Junit 和 TestNG 来支持 spring 组件的单元测试和集成测试；

- Message，相关的 jar 包包括
    - spring-messaging：包含 spring 的消息处理功能，如 Message、MessageChannel、MessageHandler；
- AOP and Instrumentation，相关的 jar 包包括
    - spring aop：提供了面向切面编程的丰富支持；
    - spring-aspects：提供了对 AspectJ 的集成；
    - spring-instrument：提供了对类 instrumentation 的支持和类加载器；
    - spring-instrument-tomcat：包含了 Spring 对 Tomcat 的 instrumentation 代理。
- Core Container: IoC 容器是 Spring 框架的核心，Spring 容器使用依赖注入管理构成应用的组件，它会创建相互协作的组件之间的关联。毫无疑问，这些对象更简单干净，更容易理解，也更容易重用和测试。Spring 自带了几种容器的实现，可归纳为两种类型。
    - BeanFactory：由 org.springframework.beans.factory.BeanFactory 接口定义。它是最简单的容器，提供基本的 DI 支持。
    - ApplicationContext：由 org.springframework.context.ApplicationContext 接口定义。它是基于 BeanFactory 之上构建，并提供面向应用的服务，例如从属性文件解析文本信息的能力，以及发布应用事件给感兴趣的事件监听者的能力。注：Bean 工厂对于大多数应用来说往往太低级，所以应用上下文使用更加广泛，推荐在开发中使用应用上下文容器。

Spring 自带了多种应用上下文，最可能遇到的有以下几种：`ClassPathXmlApplicationContext`，从类路径下的 XML 配置文件中加载上下文定义，将应用上下文定义文件当成类资源；`FileSystemXmlApplicationContext`，读取文件系统下 XML 配置文件并加载上下文定义；`XmlWebApplicationContext`，读取 Web 应用下的 XML 配置文件并装载上下文定义。

范例：
```java
ApplicationContext context = new FileSystemXmlApplicationContext("/data/source/build.xml");
ApplicationContext context2 = new ClassPathXmlApplicationContext("build.xml");
```
可以看到，加载 FileSystemXmlApplicationContext 和 ClassPathXmlApplicationContext 十分相似。差异在于：前者在指定文件系统路径下查找 build.xml 文件。通过引用应用上下文，可以很方便地调用 getBean() 方法从 Spring 容器中获取 Bean。

spring-core、spring-beans 提供了框架的基础部分，包括 IoC 和 DI 特性。spring-context 在 spring-core 和 spring-beans 基础上构建，它提供一种框架式的访问对象的方法。springcontext-support，集成第三方库到 Spring application context。spring-express，提供一种强有力的表达语言，在运行时查询和操纵一个对象图。

## 术语

- 框架：能够完成一定功能的半成品。框架做一部分功能，我们自己做一部分功能。框架否则与业务无关的抽象逻辑，我们完成业务代码。
- 非侵入式设计：从框架的角度可以这样理解，无需继承框架提供的类，这种设计就可以看做是非侵入式设计，如果继承了这些框架类，就是侵入设计，如果以后想更换框架，之前写的代码几乎无法重用，如果是非侵入框架设计，则之前写过的代码仍然可以继续使用。
- 轻量级/重量级框架：轻量级一般是非侵入式的、所依赖的东西少，资源占用少，部署简单，一般较容易使用。
- POJO：Plain Old Java Object，简单的 Java 对象，它可以包含业务逻辑或持久化逻辑，但不担当任何特殊角色且不继承或不实现其他任何Java框架的类或接口。
- 容器：装对象的对象，容器要管理被装对象的生命周期。
- IoC：由容器控制程序对象之间的关系。
- JavaBean：一般指容器管理对象，在 Spring 中指 Spring IoC 容器管理的对象。

