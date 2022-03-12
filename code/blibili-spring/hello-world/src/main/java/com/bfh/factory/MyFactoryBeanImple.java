package com.bfh.factory;

import com.bfh.Book;
import org.springframework.beans.factory.FactoryBean;

import java.util.UUID;

public class MyFactoryBeanImple implements FactoryBean<Book> {
    @Override
    public Book getObject() throws Exception {
        System.out.println("MyFactoryBeanImple create object...");
        Book book = new Book();
        book.setName(UUID.randomUUID().toString());
        return book;
    }

    @Override
    public Class<?> getObjectType() {
        return Book.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
