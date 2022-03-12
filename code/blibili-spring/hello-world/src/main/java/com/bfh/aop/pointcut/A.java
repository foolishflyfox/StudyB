package com.bfh.aop.pointcut;

import org.springframework.stereotype.Component;

@Component
public class A {

    public void foo() {
        System.out.println("A foo");
    }

    public int bar(int v) {
        System.out.println("A bar(" + v + ")");
        return v * 2;
    }

    public Integer div(int a, int b) {
        System.out.println("div(" + a + ", " + b + ")");
        return a/b;
    }
}
