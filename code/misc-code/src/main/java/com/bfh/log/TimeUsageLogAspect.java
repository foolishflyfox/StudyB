package com.bfh.log;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author benfeihu
 */
@Aspect
public class TimeUsageLogAspect {

    // 在含有指定注释的函数上使用
    @Around("@annotation(com.bfh.log.TimeUsageLog)")
    public Object logTimeUsage(ProceedingJoinPoint joinPoint) throws Throwable{
        String methodName = joinPoint.getSignature().getName();
        String args = Arrays.stream(joinPoint.getArgs()).map(String::valueOf).collect(Collectors.joining(", "));
        long timeUsage = 0;
        Object r = null;
        try {
            long t0 = System.currentTimeMillis();
            r = joinPoint.proceed();
            long t1 = System.currentTimeMillis();
            timeUsage = t1 - t0;
            return r;
        } catch (Throwable t) {
            throw  t;
        } finally {
            System.out.println(String.format("%s(%s) use %d ms", methodName, args, timeUsage));
        }
    }
}
