package com.bfh;

import lombok.Data;

@Data
public class Book {
    private String name;
    private String author;
    public Book() {
        System.out.println("create Book");
    }
    public void myInit() {
        System.out.println("book init...");
    }
    public void myDestory() {
        System.out.println("book destory...");
    }
}
