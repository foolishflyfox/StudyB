package com.bfh.ioc6.services;

import com.bfh.ioc6.bean.Book;
import com.bfh.ioc6.dao.BookDao;
import com.bfh.ioc6.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

/**
 * @author benfeihu
 */
@Service
public class BookService extends BaseService<Book> {

//    @Autowired
//    BookDao bookDao;
//
//    public void save() {
//        bookDao.save();
//    }
}
