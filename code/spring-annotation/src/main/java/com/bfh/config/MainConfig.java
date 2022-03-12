package com.bfh.config;

import com.bfh.bean.Color;
import com.bfh.bean.Person;
import com.bfh.dao.BookDao;
import org.springframework.context.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

/**
 * 配置类 = 配置文件
 * @author benfeihu
 */
@Configuration  // 告诉 Spring 这是一个配置类
@ComponentScan(value = "com.bfh", excludeFilters = {
//        @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = {Controller.class, Service.class})
}, includeFilters = {@ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Controller.class),
        @ComponentScan.Filter(type = FilterType.CUSTOM, classes = MyTypeFilter.class)},
   useDefaultFilters = false)
//@ComponentScan(value = "com.bfh", includeFilters = {
//        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {BookDao.class})
//}, useDefaultFilters = false)
// 自动扫描，value 指定要扫描的包，excludeFilters 指定扫描的时候，需要包含的组件
public class MainConfig {

    /**
     * 给容器中注册一个 bean，类型为返回值的类型，id 默认为方法名作为 id
     * @return
     */
    @Bean("person")
    public Person person01() {
        return new Person("李四", 20);
    }

}
