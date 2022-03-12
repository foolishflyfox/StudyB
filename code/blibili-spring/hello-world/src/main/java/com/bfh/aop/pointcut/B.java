package com.bfh.aop.pointcut;

import org.springframework.stereotype.Component;

@Component
public class B {
    public void foo() {
        System.out.println("B foo");
    }

    public void bar(int v) {
        System.out.println("B bar(" + v + ")");
    }
}
