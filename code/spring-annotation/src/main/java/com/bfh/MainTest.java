package com.bfh;

import com.bfh.bean.Person;
import com.bfh.config.MainConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author benfeihu
 */
public class MainTest {
    public static void main(String[] args) {
        // 注解式的容器
        ApplicationContext context = new AnnotationConfigApplicationContext(MainConfig.class);
        Person bean = context.getBean(Person.class);
        System.out.println(bean);

        String[] beanNamesForType = context.getBeanNamesForType(Person.class);
        for (String beanName : beanNamesForType) {
            System.out.println(beanName);
        }
    }
}
