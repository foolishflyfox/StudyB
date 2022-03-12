package com.bfh.config;

import com.bfh.bean.Color;
import com.bfh.bean.Person;
import com.bfh.config.condition.LinuxCondition;
import com.bfh.config.condition.MacCondition;
import com.bfh.config.condition.WindowsCondition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.*;

@Configuration
@Import({Person.class, MyImportSelector.class, MyImportBeanDefinitionRegistrar.class})
public class MainConfig2 {

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    @Lazy
    public Person person() {
        return new Person("张三", 28);
    }

    @Bean("bill")
    @Conditional(WindowsCondition.class)
    public Person person01() {
        return new Person("Bill Gates", 62);
    }

    @Bean("linus")
    @Conditional(LinuxCondition.class)
    public Person person02() {
        return new Person("linus", 48);
    }

    @Bean("jobs")
    @Conditional({MacCondition.class})
    public Person perso03() {
        return new Person("Stave Jobs", 61);
    }

    @Bean
    public ColorFactoryBean cFactoryBean() {
        return new ColorFactoryBean();
    }
}
