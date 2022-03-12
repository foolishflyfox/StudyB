package com.bfh;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SourceTest {

    public static void main(String[] args) {
        ApplicationContext ioc = new ClassPathXmlApplicationContext("ioc2.xml");
        Object bean = ioc.getBean("person3");
        System.out.println(bean);
    }
}
