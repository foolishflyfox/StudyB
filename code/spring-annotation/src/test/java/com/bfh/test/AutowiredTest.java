package com.bfh.test;

import com.bfh.bean.Color;
import com.bfh.bean.Red;
import com.bfh.bean.work.Boss;
import com.bfh.bean.work.Car;
import com.bfh.config.MainConfigOfAutowired;
import com.bfh.dao.BookDao;
import com.bfh.service.BookService;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Arrays;

public class AutowiredTest {
    @Test
    public void test01() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MainConfigOfAutowired.class);
        context.getBean(BookService.class).print();
        System.out.println(context.getBean(Boss.class).getCar());
        System.out.println(context.getBean(Car.class));
        System.out.println(context.getBean(Color.class).getCar());
//        System.out.println(context.getBean(BookDao.class));
//        String[] beanDefinitionNames = context.getBeanDefinitionNames();
//        System.out.println(Arrays.toString(beanDefinitionNames));
//        System.out.println(beanDefinitionNames.length);

    }

    @Test
    public void test02() {
        ApplicationContext context = new AnnotationConfigApplicationContext(MainConfigOfAutowired.class);
        System.out.println(context);
//        System.out.println(context.getBeansOfType(Red.class));
    }
}
