package com.bfh.test;

import com.bfh.bean.Person;
import com.bfh.config.MainConfig;
import com.bfh.config.MainConfig2;
import org.junit.Test;
import org.springframework.beans.factory.Aware;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;

import java.util.Map;

/**
 * @author benfeihu
 */
public class IocTest {

    @Test
    public void test01() {
        ApplicationContext context = new AnnotationConfigApplicationContext(MainConfig.class);
        String[] beanDefinitionNames = context.getBeanDefinitionNames();
        for (String beanDefinitionName : beanDefinitionNames) {
            System.out.println(beanDefinitionName);
        }
    }

    @Test
    public void test02() {
        ApplicationContext context = new AnnotationConfigApplicationContext(MainConfig2.class);
        System.out.println("after create container");
//        Object person1 = context.getBean("person");
//        Object person2 = context.getBean("person");
//
//        System.out.println(person1 == person2);
    }

    @Test
    public void test03() {
        ApplicationContext context = new AnnotationConfigApplicationContext(MainConfig2.class);

        // 获取环境变量的值
        Environment environment = context.getEnvironment();
        System.out.println(environment.getProperty("os.name"));

        for (Map.Entry<String, Person> entry : context.getBeansOfType(Person.class).entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }
    }

    @Test
    public void testImport() {
        ApplicationContext context = new AnnotationConfigApplicationContext(MainConfig2.class);
        printBeans(context);
        Object bean1 = context.getBean("&cFactoryBean");
        Object bean2 = context.getBean("cFactoryBean");
        Object bean3 = context.getBean("cFactoryBean");
//        System.out.println(bean1);
//        System.out.println(bean2);
//        Object bean3 = context.getBean("&colorFactoryBean");
//        System.out.println(bean3);

    }

    private void printBeans(ApplicationContext applicationContext) {
        for (String name : applicationContext.getBeanDefinitionNames()) {
            System.out.println(name);
        }
    }
}
