package com.bfh.config;

import com.bfh.aop.LogAspect;
import com.bfh.aop.MathCalculator;
import org.aspectj.lang.annotation.Before;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * AOP: 指在程序运行期间动态地将某段代码切入到指定方法指定位置运行的方式
 */
@Configuration
@EnableAspectJAutoProxy // 启用基于注解的 AOP 模式
@ComponentScan("com.bfh.aop")
public class MainConfigOfAOP {
//    @Bean
//    public MathCalculator calculator() {
//        return new MathCalculator();
//    }
//    @Bean
//    public LogAspect logAspect() {
//        return new LogAspect();
//    }
}
