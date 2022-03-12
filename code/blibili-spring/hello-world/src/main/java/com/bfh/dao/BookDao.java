package com.bfh.dao;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Repository
//@Scope("prototype")
public class BookDao {

    public void saveBook() {
        System.out.println("book has saved...");
    }

}
