package com.bfh.config.spring;

import com.bfh.log.TimeUsageLogAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @author benfeihu
 */
@Configuration
@EnableAspectJAutoProxy
public class CommonConfig {

    @Bean
    public TimeUsageLogAspect timeUsageLog() {
        return new TimeUsageLogAspect();
    }
}
