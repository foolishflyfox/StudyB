package com.bfh;

import com.bfh.tx.BookDao;
import com.bfh.tx.BookService;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TxTest {

    ApplicationContext ioc = new ClassPathXmlApplicationContext("tx.xml");

    @Test
    public void test() {
        BookService bookService = ioc.getBean(BookService.class);
        bookService.checkout("Tom", "ISBN-001");
        System.out.println("结账完成");
    }
}
