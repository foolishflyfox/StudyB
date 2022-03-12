# spring 的注解开发

- 声明一个类是 spring 组件
    - `@Component`: 普通组件
    - `@Repository`: DAO 组件
    - `@Controller`: 控制器组件
    - `@Service`: 业务层组件
- 将普通类实例成组件加入容器
    - `@Bean(组件id)`: *initMethod* 和 *destroyMethod* 指定初始化和销毁方法，单实例 bean 是在容器关闭的时候调用销毁函数，容器不负责销毁多实例 bean ；
    - `@Import({A.class, B.class})`: 默认 id 为全类名；如果其中的类实现了 `org.springframework.context.annotation.ImportSelector`，并不是指导入该类，而是导入该类通过全类名指定的类；使用 `ImportBeanDefinitionRegistrar` 的实现类动态指定；使用 Spring 提供的 FactoryBean，默认获取到的是工厂 bean 调用 getObject 创建的对象，要获取工厂 Bean 本身，我们需要给 id 前面加一个 `&` 标识；
- 声明一个类是 Spring 的 配置类
    - `@Configuration`: 通过 `new AnnotationConfigApplicationContext(配置类.class)` 可以根据配置类初始化 ioc 容器
        - `@ComponentScan`注释，设置自动扫描
            - *value* : 指定扫描的包，如 `com.bfh`;
            - *includeFilters* / *excludeFilters* : 指定包含/排除的过滤器
                - *type*: 指定过滤类型，如 ANNOTATION 按注解过滤 / ASSIGNABLE_TYPE 按类型过滤 / ASPECTJ / REGEX / CUSTOM
                - *classes*: 指定过滤值
            - *useDefaultFilters*: true(默认)，包含指定包下所有类；false，排除包下所有类
- `@Scope`: 声明组件作用范围
    - `ConfigurableBeanFactory.SCOPE_SINGTON`: 单实例的；
    - `ConfigurableBeanFactory.SCOPE_PROTOTYPE`: 多实例的；
- `@Lazy`: 单实例懒加载
- `@Conditional`: 条件加载，是 spring 底层大量使用的注解。按照一定的条件进行判断，如果满足条件，就在容器中注册 bean。也可以放在 类上，表示满足条件时，这个类中的配置才生效
- Bean 的生命周期
    - `@Bean(组件id)`: *initMethod* 和 *destroyMethod* 指定初始化和销毁方法；用 `@Bean` 标注方法创建对象的时候，方法参数值从容器中获取；
    - 通过让 Bean 实现 InitializingBean 来定义初始化逻辑，通过实现 DisposableBean 来定义销毁逻辑；
    - 可以使用 JSR 250 (需要引入 `org.projectlombok:lombok`)：
        - `@PostConstruct`: 在 bean 创建完成并且属性赋值完成，来执行初始化方法；
        - `@PreDestroy`: 在容器销毁 bean 之前通知我们进行清理工作；
    - `BeanPostProcessor`的实现类加入到容器中: bean 的后置处理器，在 bean 初始化前后进行一些处理工作；
        - `postProcessBeforeInitialization`: 在初始化之前工作；
        - `postProcessAfterInitialization`: 在初始化之后工作；
- Bean 属性赋值 `@Value`，`@Value` 类似于 spring xml 配置时的 value 属性，但是使用注解时，不要求被配置对象一定要有 setter 方法。
    - `@Value("abc")`: 属性复制 “abc”；
    - `@Value("#{23*11}")`: 通过 SpEL 设置值，还可以 `@Value("#{miscUtils.getNickName()}")` 调用容器中对象的函数获取值；
    - `@Value("${person.email}")`: 通过配置指定；通过 `@PropertySource` 可以指定属性文件的位置；
- 自动装配：Spring 利用依赖注入 DI，完成对 IOC 容器中各个组件的依赖关系赋值
    - `@Autowired`: 自动注入，1、默认优先按照类型找对应的组件，找到即赋值；2、如果找到多个，就将属性名称作为组件的 id 去容器中查找；3、通过 `@Qualifier("bookDao")` 指定要装配的组件 id；4、自动装配默认一定要将属性赋值好，没有就会报错；5、非必须装配(打破4的限制)，通过 `@Autowired(required = false)` 实现；6、`@Primary` 指定首选 bean，让 Spring 进行自动装配时，默认使用首选的 bean；
    - Spring 还支持使用 `@Resource`(JSR250) 和 `@Inject`(JSR330)
        - `@Resource`: 默认是按照组件名称进行装配的，没有能支持 `@Primary` 功能，也没支持 `@Autowired(required=false)`
        - `@Inject`: 需要导入 javax.inject 包，和 `@Autowired` 功能类似；
    - 构造器、参数、方法、属性位置都能使用 `@Autowired`
- 自定义组件想要使用 Spring 容器底层的一些属性(ApplicationContext / BeanFactory.xxx)，只需要自定义组件实现 xxxAware，在创建对象的时候，会调用接口规定的方法注入相关组件；
    - `ApplicationContextAware`: 可以获取容器；
    - `BeanNameAware`: 可以获取 bean id；
    - `EmbeddedValueResolverAware`: 获取容器解析器；
- `@Profile`: 指定环境，根据环境动态加载组件；


包含/排除的规则：
- `FilterType.ANNOTATION`: 按照注解
- `FilterType.ASSIGNABLE_TYPE`: 指定类
- `FilterType.ASPECTJ`: 使用 ASPECTJ ，不常用
- `FilterType.REGEX`: 使用正则表达式，不常用
- `FilterType.CUSTOM`: 自定义过滤规则

自定义规则 demo：
```java
public class MyTypeFilter implements TypeFilter {
    /**
     * @param metadataReader 读取到的当前正在扫描的类的信息
     * @param metadataReaderFactory 可以获取到的其他类的信息
     * @return
     * @throws IOException
     */
    @Override
    public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
        // 获取当前类注解的信息
        AnnotationMetadata annotationMetadata = metadataReader.getAnnotationMetadata();
        // 获取当前正在扫描的类的类信息
        ClassMetadata classMetadata = metadataReader.getClassMetadata();
        // 获取当前类资源，如类的路径
        Resource resource = metadataReader.getResource();

        String className = classMetadata.getClassName();
        System.out.println(" --> " + className);
        return className.endsWith("Dao");
    }
}
```

AOP :
1. 导入 aop 模块：Spring AOP （spring-aspects）
2. 定义一个业务逻辑类(MathCalculator)，在业务逻辑运行的时候将日志打印(方法运行前、运行后、返回后、抛异常后等)
3. 定义一个日志切面类(LogAspects)，切面类里面的方法需要动态感知 MathCalculator.div 运行到的方法
    通知方法：
        前置通知 `@Before`
        后置通知 `@After`
        返回通知 `@AfterReturning`
        异常通知 `@AfterThrowing`
        环绕通知 `@Around`
4. 给切面类的目标方法标注合适何地运行；
5. 将切面类和业务逻辑类(目标方法所在类)都加入到容器中；
6. 切面类加注解 `@Aspect`
7. 添加 `@EnableAspectJAutoProxy` 表示自动引入注解；

