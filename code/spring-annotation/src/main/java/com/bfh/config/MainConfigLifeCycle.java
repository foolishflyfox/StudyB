package com.bfh.config;

import com.bfh.bean.work.Car;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * bean 的生命周期：bean创建 -- 初始化 -- 销毁
 * 容器管理 bean 的生命周期
 * 我们可以自定义初始化和销毁方法，容器在 bean 进行到当前生命周期的时候来调用我们自定义的初始化和销毁方法
 * 1）指定初始化和销毁方法
 *
 * @author benfeihu
 */
@Configuration
@ComponentScan("com.bfh.bean")
public class MainConfigLifeCycle {
    @Bean(initMethod = "init", destroyMethod = "destroy")
    @Scope("prototype")
    public Car car() {
        return new Car();
    }
}
