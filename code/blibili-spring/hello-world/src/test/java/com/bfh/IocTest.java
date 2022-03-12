package com.bfh;

import com.bfh.service.BookService;
import com.bfh.servlet.BookServlet;
import com.google.common.collect.Lists;
import com.bfh.dao.BookDao;
import lombok.ToString;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Map;
import java.util.Optional;

public class IocTest {

    /**
     * 从容器中拿到组件
     */
    @Test
    public void test() {
        // ApplicationContext，代表 ioc 容器
        // 当前应用的 xml 配置文件在 ClassPath 下
        ApplicationContext ioc = new ClassPathXmlApplicationContext("ioc.xml");
//         通过 bean id 找到对象
//        Person person01 = (Person) ioc.getBean("person01");
//        Person person02 = ioc.getBean("p2", Person.class);
//        System.out.println(person01 == person02);
//        Person person3 = ioc.getBean("person3", Person.class    );
        System.out.println(ioc.getBean(Person.class));
    }

    @Test
    public void test2() {
        ApplicationContext ioc2 = new ClassPathXmlApplicationContext("ioc2.xml");
        Person person = ioc2.getBean("person", Person.class);
        Car car = ioc2.getBean(Car.class);
        System.out.println();
        System.out.println(person);
        System.out.println(car == person.getCar());
        Person person2 = ioc2.getBean("person2", Person.class);
        System.out.println(person2);
        Person person3 = ioc2.getBean("person3", Person.class);
        System.out.println(person3);
        Person person4 = ioc2.getBean("person4", Person.class);
        System.out.println(person4);
        System.out.println(person4.getMaps().get("a").getClass().getName());
        // 生成的为 java.util.LinkedHashMap 类型
        System.out.println(person4.getMaps().getClass().getName());
        Person person5 = ioc2.getBean("person5", Person.class);
        System.out.println(person5.getProperties());
//        Book innerBook = ioc2.getBean("innerBook", Book.class);
//        System.out.println(innerBook);
        Person person6 = ioc2.getBean("person6", Person.class);
        System.out.println(person6);
        Map<String, Object> myMap = (Map<String, Object>)ioc2.getBean("myMap");
        System.out.println(myMap);
        System.out.println(myMap.get("ccc").getClass().getName());

        Person person7 = ioc2.getBean("person7", Person.class);
        System.out.println(person7);
    }

    @Test
    public void test3() {
        ApplicationContext context = new ClassPathXmlApplicationContext("ioc2.xml");
        Person person7 = context.getBean("person7", Person.class);
        System.out.println(person7);
        Person person6 = context.getBean("person6", Person.class);
//        System.out.println(person6);
    }

    @Test
    public void test4() {
        ApplicationContext context = new ClassPathXmlApplicationContext("ioc3.xml");
        System.out.println("start ioc container");
        Book book1 = context.getBean("book", Book.class);
        Book book2 = context.getBean("book", Book.class);
        System.out.println(book1==book2);
    }

    @Test
    public void test5() {
        ApplicationContext context = new ClassPathXmlApplicationContext("ioc3.xml");
        System.out.println("context created");
        AirPlane airPlane01 = context.getBean("airPlane01", AirPlane.class);
        System.out.println(airPlane01);
        AirPlane airPlane02 = context.getBean("airPlane02", AirPlane.class);
        System.out.println(airPlane02);
        Object tmp1 = context.getBean("tmp");
        System.out.println(tmp1.getClass().getName());
        Object tmp2 = context.getBean("tmp");
        System.out.println(tmp1 == tmp2);
    }

    @Test
    public void test2_1() {
        ConfigurableApplicationContext context = new ClassPathXmlApplicationContext("test2.xml");
        System.out.println("context created");
        // 多实例不会调用 destory
        Book book01 = context.getBean("book01", Book.class);
        System.out.println(book01);
        System.out.println(context.getBean(Car.class));
        context.close();
    }

    @Test
    public void test3_1() {
        ConfigurableApplicationContext context = new ClassPathXmlApplicationContext("ioc4.xml");
        Person person = context.getBean("person", Person.class);
//        System.out.println(person);
        Person person1 = context.getBean("person2", Person.class);
        System.out.println(person1);
    }

    @Test
    public void test6() {
        Lists.partition(Lists.newArrayList(1,2,3,4,5,6,7), 2).forEach(System.out::println);
    }

    @Test
    public void ioc5Test() {
        ConfigurableApplicationContext ioc5 = new ClassPathXmlApplicationContext("ioc5.xml");
//        Object bookDao1 = ioc5.getBean("haha");
//        Object bookDao2 = ioc5.getBean("haha");
//        System.out.println(bookDao1==bookDao2);
//        Object bookServlet = ioc5.getBean("bookServlet");
//        System.out.println(bookServlet);

        BookService bookService = ioc5.getBean(BookService.class);
        System.out.println(Optional.ofNullable(bookService)
                .map(BookService::getBookDaoExtend2)
                .map(Object::getClass)
                .map(Class::getName)
                .orElse(null));
//        System.out.println(bookService.getBookDaoExtend2());
//        System.out.println(bookService.getBookDaoExtend2().getClass().getName());

    }

    @Test
    public void test18() {
        ApplicationContext ioc5 = new ClassPathXmlApplicationContext("ioc5.xml");
        BookServlet bookServlet = ioc5.getBean(BookServlet.class);
        bookServlet.doSave();
    }

}
