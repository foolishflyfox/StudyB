package com.bfh;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

/**
 * @author benfeihu
 * 主程序类
 */
@SpringBootApplication  // 告诉 springboot ，这是一个 springboot 应用
public class MainApp {
    public static void main(String[] args) {
        // 固定写法，让 springboot 跑起来
        SpringApplication.run(MainApp.class, args);
    }
}
