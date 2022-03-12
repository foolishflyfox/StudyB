package com.bfh.config;

import com.bfh.bean.Person;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

// 使用 @PropertySource 读取外部配置文件中 k/v 保存到运行的环境变量中；加载完外部的配置文件以后，
// 使用 ${} 取出配置文件的值
@Configuration
@PropertySource(value = {"classpath:/person.properties"})
public class MainConfigOfPropertyValues {

    @Bean
    public Person person() {
        return new Person();
    }

    @Bean
    public MiscUtils miscUtils() {
        return new MiscUtils();
    }

}
