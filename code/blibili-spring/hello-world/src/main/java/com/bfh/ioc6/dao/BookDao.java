package com.bfh.ioc6.dao;

import com.bfh.ioc6.bean.Book;
import org.springframework.stereotype.Repository;

/**
 * @author benfeihu
 */
@Repository
public class BookDao extends BaseDao <Book> {
    @Override
    public void save() {
        System.out.println("保存图书");
    }
}

