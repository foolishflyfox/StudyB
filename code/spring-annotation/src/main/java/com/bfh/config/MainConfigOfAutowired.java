package com.bfh.config;

import com.bfh.bean.Color;
import com.bfh.bean.Red;
import com.bfh.bean.work.Car;
import com.bfh.dao.BookDao;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * 自动装配：Spring 利用依赖注入 DI，完成对 IOC 容器中各个组件的依赖关系赋值
 */
@Configuration
//@ComponentScan({"com.bfh.controller", "com.bfh.service"})
//@ComponentScan("com.bfh.dao")
//@ComponentScan("com.bfh.bean.work")
public class MainConfigOfAutowired {

//    @Bean
//    @Primary
    BookDao bookDao2() {
        BookDao result = new BookDao();
        result.setLabel("2");
        return result;
    }

//    @Bean
    Color color(Car car) {
        Color color = new Color();
        color.setCar(car);
        return color;
    }

    @Bean
    Red red_x() {
        return new Red();
    }

}
