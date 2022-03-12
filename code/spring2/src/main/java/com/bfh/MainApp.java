package com.bfh;

import com.bfh.bean.Person;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;

/**
 * @author benfeihu
 */
public class MainApp {
    public static void main(String[] args) {
//        ApplicationContext ctx =
//                new ClassPathXmlApplicationContext("bean.xml");
//        Person person1 = (Person)ctx.getBean("person");
//        Person person2 = (Person)ctx.getBean("person");
//        System.out.println(person1 == person2);
        BeanFactory container = new XmlBeanFactory(new ClassPathResource("bean.xml"));
        Person person1 = (Person) container.getBean("person");

    }
}
