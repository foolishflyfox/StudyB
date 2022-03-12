package com.bfh.xmlaop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;

import java.util.Arrays;

/**
 * @author benfeihu
 */
public class LogUtil {

    public void before(JoinPoint joinPoint) {
        System.out.println(this.getClass().getName() + " before");
    }

    public void logReturn(JoinPoint joinPoint, Object result) {
        System.out.println("LogUtil " + joinPoint.getSignature().getName() +
                Arrays.toString(joinPoint.getArgs()) + " = " + result);
    }

    public void logEnd() {
        System.out.println("LogUtil logEnd");
    }

    public Object around(ProceedingJoinPoint pjp) {
        System.out.println(getClass().getName() + " around before");
        try {
            Object r = pjp.proceed(pjp.getArgs());
            return r;
        } catch (Throwable e) {
            System.out.println(getClass().getName() + " around after throwing ");
            throw new RuntimeException(e);
        } finally {
            System.out.println(getClass().getName() + " after");
        }
    }

}
