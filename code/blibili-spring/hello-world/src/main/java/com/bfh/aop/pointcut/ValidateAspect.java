package com.bfh.aop.pointcut;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Order(1000)
public class ValidateAspect {

    @Around("execution(* com.bfh.aop.pointcut.A.*(..))")
    public Object around(ProceedingJoinPoint pjp) {
        System.out.println("validate before");
        Object r = null;
        try {
            r = pjp.proceed(pjp.getArgs());
            System.out.println("validate after returning");
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            System.out.println("validate after throwing");
        } finally {
            System.out.println("validate after");
        }
        return r;
    }

    @Before("execution(* com.bfh.aop.pointcut.B.*(..))")
    void before() {
        System.out.println("validate before");
    }

    @After("execution(* com.bfh.aop.pointcut.B.*(..))")
    void after() {
        System.out.println("validate after");
    }

    @AfterReturning("execution(* com.bfh.aop.pointcut.B.*(..))")
    void afterReturning() {
        System.out.println("validate after returning");
    }
}
