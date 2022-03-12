package com.bfh;

import com.bfh.aop.pointcut.A;
import com.bfh.aop.pointcut.B;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class PointCutTest {

    @Test
    public void t1() {
        ApplicationContext context = new ClassPathXmlApplicationContext("pointcut.xml");
        A a = context.getBean(A.class);
        B b = context.getBean(B.class);
//        a.foo();
//        a.bar(12);
//        B b = context.getBean(B.class);
//        b.foo();
//        b.bar(15);
        b.foo();
    }

}
