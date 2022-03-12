package com.bfh.controller;

import com.bfh.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * @author benfeihu
 */
@Controller
public class BookController {

    @Autowired
    private BookService bookService;
}
