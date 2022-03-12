package com.bfh.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.stream.Collectors;

@Aspect
@Component
public class LogAspect {

    @Pointcut("execution(public int com.bfh.aop.MathCalculator.*(..))")
    public void pointCut() {}

    // 指定了 args(v1, v2) 表示切的是有两个参数的变量
    @Before(value = "pointCut() && args(v1, v2)", argNames = "v1,v2")
    public void logStart(Object v1, Object v2) {
        System.out.println(String.format("运行。。。参数列表是：%s, %s", v1, v2));
    }

    @After("pointCut()")
    public void logEnd(JoinPoint joinPoint) {
        System.out.println(joinPoint.getSignature().getName() + " 结束。。。");
    }

    @AfterReturning(value = "pointCut()", returning = "result")
    public void logReturn(JoinPoint joinPoint, Object result) {
        System.out.println(String.format("%s(%s) 结果 = %s",
                joinPoint.getSignature().getName(),
                Arrays.stream(joinPoint.getArgs()).map(Object::toString).collect(Collectors.joining(", ")),
                result));
    }

    // JoinPoint 参数一定要出现在参数表的第一位
    @AfterThrowing(value = "pointCut()", throwing = "exception")
    public void logExcept(JoinPoint joinPoint, Exception exception) {
        System.out.println(joinPoint.getSignature().getName() + " 抛出异常" + exception);
    }
}
