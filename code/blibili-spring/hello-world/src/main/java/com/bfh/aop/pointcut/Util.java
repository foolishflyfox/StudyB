package com.bfh.aop.pointcut;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Order(1001)
public class Util {

    final String aopAExp = "execution(* com.bfh.aop.pointcut.A.*(..))";
    final String aopBExp = "execution(* com.bfh.aop.pointcut.B.*(..))";

//    @Pointcut

//    @Before(aopExp)
//    void before() {
//        System.out.println("before process");
//    }
//
//    @After(aopExp)
//    void after() {
//        System.out.println("after process");
//    }
//
//    @AfterReturning(value = aopExp, returning = "result")
//    void afterReturning(Object result){
//        System.out.println("afterReturning result = " + result);
//    }

    @Around(aopAExp)
    Object around(ProceedingJoinPoint pjp) throws Throwable {
        Object[] args = pjp.getArgs();
        Object result = null;
        try {
            System.out.println(pjp.getSignature().getName() + " "
                    + Arrays.toString(args) + " before");
            result = pjp.proceed(args);
            System.out.println(pjp.getSignature().getName() + " "
                    + Arrays.toString(args) + " afterReturning");
        } catch (Throwable e) {
            // AfterThrowing
            System.out.println(pjp.getSignature().getName() + " "
                    + Arrays.toString(args) + " exception " + e);
            throw new RuntimeException(e);
        } finally {
            // After
            System.out.println(pjp.getSignature().getName() + " "
                    + Arrays.toString(args) + " After");
        }
        return result;
    }

    @Before(aopBExp)
    void before() {
        System.out.println("util before");
    }

    @After(aopBExp)
    void after() {
        System.out.println("util after");
    }

    @AfterReturning(aopBExp)
    void afterReturning() {
        System.out.println("util after returning");
    }

}
