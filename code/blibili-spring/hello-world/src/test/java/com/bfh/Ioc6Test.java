package com.bfh;

import com.bfh.ioc6.services.BookService;
import com.bfh.ioc6.services.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author benfeihu
 */
@ContextConfiguration(locations = "classpath:ioc6.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class Ioc6Test {
//    ApplicationContext ioc = new ClassPathXmlApplicationContext("ioc6.xml");

    @Autowired
    BookService bookService;
    @Autowired
    UserService userService;

    @Test
    public void test1() {
//        BookService bookService = ioc.getBean("bookService", BookService.class);
//        UserService userService = ioc.getBean("userService", UserService.class);
        System.out.println(bookService);
        bookService.save();
        userService.save();
        System.out.println(bookService.getClass().getGenericSuperclass());
    }
}
