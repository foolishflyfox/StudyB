package com.bfh.bean.work;

import org.springframework.stereotype.Component;

/**
 * @author benfeihu
 */
@Component
public class Car {
    public Car() {
        System.out.println("car constructor ...");
    }
    public void init() {
        System.out.println("car ...init...");
    }
    public void destroy() {
        System.out.println("car ...destroy...");
    }
}
