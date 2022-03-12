# JUnit4

JUnit 是 java 编程语言的单元测试框架，用于编写和运行可重复的自动化测试。

## JUnit 的特点

JUnit 是一个开放的资源框架，用于编写和运行测试。

- 提供注解来识别注册方法；
- 提供断言来测试预期结果；
- 也许你编写代码更快，并能够提高质量；
- 简洁优雅；
- 自动运行并检查自身结果，提供及时反馈；
- 可以被组织为测试套件，包含测试用例，甚至其他的测试套件；

## 常用注解

- `@Test`: 定义一个测试方法，测试方法必须是 public void。
- `@Ignore`: 暂不执行该方法。
- `@BeforeClass`: 在测试类中所有用例执行前，运行一次该方法，例如创建数据库连接、读取文件等。注意，方法名可以任意，但必须是 public static void。
- `@AfterClass`: 在测试类中运行所有用例后，运行一次。用于处理一些测试后续工作，例如，清理数据，恢复现场。必须是 public static void。
- `@Before`: 不止运行一次，会在每个用例运行之前都运行一次。主要用于一些独立于用例之间的准备工作。比如两个用例都需要读取数据库中的用户 A 信息，但是第一个用例会删除这个用户 A，第二个用例需要修改用户 A。那么可以用 `@BeforeClass` 建立数据库连接。用 `@Before` 插入一条用户 A 信息。必须是 public void。
- `@After`: 与 `@Before` 相对。
- `@Runwith`: 首先要分清几个概念：测试方法、测试类、测试集、测试运行器。
    - 测试方法：用 `@Test` 注解的函数；
    - 测试类：包含一个或多个测试方法的一个类；
    - 测试集：一个 suite，可以包含多个测试类；
    - 测试运行器：决定了用什么方式偏好去运行这些测试集、类、方法。
    - `@Runwith` 就是放在测试类名之前，用于确定这个类怎么运行的。也可以不标注，会使用默认运行器，常见的运行器有：
        - `@Runwith(Parameterized.class)`: 参数化运行器，配合 `@Parameters` 使用 junit 的参数化功能；
        - `@Runwith(Suite.class) @SuiteClasses({ATest.class, BTest.class, CTest.class})`: 测试集运行器配合使用测试集功能；
        - `@Runwith(JUnit4.class)`: junit4 的默认运行器；
        - `@RunWith(JUnit38ClassRunner.class)`: 用于兼容 junit3.8 的运行器；
        - 一些其他运行器具备更多功能。例如 `@RunWith(SpringJUnit4ClassRunner.class)`: 集成了 spring 的一些功能。
- `@Parameters`: 用于使用参数化功能；

## 测试：断言测试

- `void assertEquals(String message, expected, actual)`： 断言两个值相等，值类型可能是 int、short、long、byte、char、Object。第一个参数是一个可选字符串消息。
- `assertTrue`
- `assertFalse`
- `assertNotNull`
- `assertNull`
- `assertSame`: 断言两个对象引用相同；
- `assertNotSame`
- `assertArrayEquals`: 断言预期数组和结果数组相等；判断元素是否相等也是用 equals 方法。

## 测试：异常测试

`@Test(expected = xxx)`。Junit 用代码处理提供了一个追踪异常的选项。你可以测试代码是否抛出了你想要的异常，expected 参数和 `@Test` 注释一起使用。

## 测试：运行超时测试

`@Test(timeout = xxx)`。超过指定时间即为测试不通过。
