package com.bfh.config;

import com.bfh.auto.Cat;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author benfeihu
 */
@Configuration
@ComponentScan(value = "com.bfh.auto")
public class AutoConfig {
    @Bean
    Cat cat() {
        return new Cat();
    }
}
