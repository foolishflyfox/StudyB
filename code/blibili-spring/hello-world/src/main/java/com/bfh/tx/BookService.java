package com.bfh.tx;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookService {

    @Autowired
    BookDao bookDao;

    @Transactional
    public void checkout(String userName, String isbn) {
        // 1. 减库存
        if(0==bookDao.updateStock(isbn)) {
            throw new RuntimeException("减库存出错");
        }
        // 2. 查价格
        int price = bookDao.getPrice(isbn);
        System.out.println("price = " + price);
        // 3. 减余额
        if(0 == bookDao.updateBalance(userName, price)) {
            throw new RuntimeException("减余额出错");
        }

    }
}
