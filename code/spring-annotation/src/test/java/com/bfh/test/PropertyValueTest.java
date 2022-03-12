package com.bfh.test;

import com.bfh.bean.Person;
import com.bfh.config.MainConfigOfPropertyValues;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class PropertyValueTest {

    @Test
    public void test01() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MainConfigOfPropertyValues.class);
//        printBeans(context);
        System.out.println(context.getBean(Person.class));
        // 获取配置文件中定义的属性
//        System.out.println(context.getEnvironment().getProperty("person.email"));
        context.close();
    }

    private void printBeans(ApplicationContext applicationContext) {
        for (String name : applicationContext.getBeanDefinitionNames()) {
            System.out.println(name);
        }
    }
}
