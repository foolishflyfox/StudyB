package com.bfh.config.spring;

import com.bfh.stream.PerformanceDemo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author benfeihu
 */
@Configuration
@Import({PerformanceDemo.class})
public class TempConfig {
}
