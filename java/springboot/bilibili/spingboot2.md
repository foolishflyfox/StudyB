
`@Configuration`: 告诉 springboot 这是一个配置类，等同于之前的配置文件。

`@SpringBootApplication` 是一个合成注解，它包括3个注解：
- `@SpringBootConfiguration`: 代表当前是一个配置类；
- `@ComponentScan(excludeFilters = { @Filter(type = FilterType.CUSTOM, classes = TypeExcludeFilter.class),@Filter(type = FilterType.CUSTOM, classes = AutoConfigurationExcludeFilter.class) })`
- `@EnableAutoConfiguration`
    - `@AutoConfigurationPackage`
        - `@Import(AutoConfigurationPackages.Registrar.class)`
