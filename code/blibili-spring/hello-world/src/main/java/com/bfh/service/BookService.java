package com.bfh.service;

import com.bfh.dao.BookDao;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class BookService {

    @Getter
    @Resource
    @Qualifier("bookDaoExtend")
//    @Autowired(required = false)
    BookDao bookDaoExtend2;

    public void save() {
        System.out.println("bookService >.. call dao to save book");
        if (null != bookDaoExtend2) {
            bookDaoExtend2.saveBook();
        }
    }

    @Autowired
    public void foo(@Qualifier("bookDaoExtend") BookDao bookDao) {
        System.out.println("foo is called : " + bookDao);
    }

}
