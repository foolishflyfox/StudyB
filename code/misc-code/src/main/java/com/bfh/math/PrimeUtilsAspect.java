package com.bfh.math;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author benfeihu
 */
@Aspect
@Component
public class PrimeUtilsAspect {

    @Pointcut("execution(* com.bfh.math.PrimeUtils.*(..))")
    public void pointcut() {}

//    @Before("pointcut()")
//    public void before() {
//        System.out.println("xxxxxxx");
//    }
    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) {
        Object result = null;
        try {
            long t0 = System.currentTimeMillis();
            result = joinPoint.proceed(joinPoint.getArgs());
            long t1 = System.currentTimeMillis();
            System.out.println(String.format("%s(%s) : %d ms", joinPoint.getSignature().getName(),
                    Stream.of(joinPoint.getArgs()).map(String::valueOf).collect(Collectors.joining(", ")),
                    t1-t0));
        } catch (Throwable throwable) {

        } finally {

        }
        return result;
    }

}
