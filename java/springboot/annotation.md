springboot 的注解归纳。

- @Bean("tom")
- @Component
- @Controller
- @Repository
- @Service
- @Autowired
- @Import({XXX.class})
- @Configuration(proxyBeanMethods = true)
- @EnableConfigurationProperties(Car.class)
- @ConditionalOnBean(name={"tom"})
- @SpringBootApplication(scanBasePackage="com")   可以改变包扫描路径
- @ComponentScan
- @SpringBootApplication


- @ResponseBody
- @RequestMapping("/car")

