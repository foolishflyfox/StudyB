package com.bfh.test;

import com.bfh.aop.MathCalculator;
import com.bfh.config.MainConfigOfAOP;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class AopTest {
    @Test
    public void test01() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MainConfigOfAOP.class);
        MathCalculator bean01 = context.getBean(MathCalculator.class);
        System.out.println("result = " + bean01.add(6,3));
    }
}
