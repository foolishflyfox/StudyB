package com.bfh.bean;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;


@Component
public class Dog {

    public Dog() {
        System.out.println("Dog contructor...");
    }

    // 对象创建并赋值之后调用
    @PostConstruct
    public void init() {
        System.out.println("Dog...@PostConstruct...");
    }

    // 容器移除对象之前
    @PreDestroy
    public void destory() {
        System.out.println("Dog...@PreDestroy...");
    }
}
