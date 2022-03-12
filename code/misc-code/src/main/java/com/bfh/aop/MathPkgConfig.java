package com.bfh.aop;

import com.bfh.math.PrimeUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @author benfeihu
 */
@Configuration
@ComponentScan("com.bfh.math")
@EnableAspectJAutoProxy
public class MathPkgConfig {

    @Bean
    public PrimeUtils primeUtils() {
        return new PrimeUtils();
    }
}
