package com.bfh;

import com.bfh.bean.A;
import com.bfh.bean.Person;
import com.bfh.controller.HelloController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

/**
 * @author benfeihu
 * 主程序类
 */
@SpringBootApplication  // 告诉 springboot ，这是一个 springboot 应用
public class MainApp {
    public static void main(String[] args) {
        // 固定写法，让 springboot 跑起来
        ApplicationContext context = SpringApplication.run(MainApp.class, args);
        System.out.println(context.getBean(HelloController.class));
    }

    @Bean
    public A a1() {
        return new A("a1");
    }
    @Bean
    public A a2() {
        return new A("a2");
    }
}
