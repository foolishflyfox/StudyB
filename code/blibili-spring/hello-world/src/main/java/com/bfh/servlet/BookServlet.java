package com.bfh.servlet;

import com.bfh.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class BookServlet {

    // 自动装配，自动为这个属性赋值
    @Autowired
    private BookService bookService;

    public void doSave() {
        bookService.save();
    }

}
