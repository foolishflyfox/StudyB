package com.bfh.aop.utils;

import com.bfh.aop.impl.MyMathCalculator;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.stream.Collectors;

@Component
@Aspect  // 告诉 spring 这是一个切面类
public class LogUtil {

    /**
     * @Before 前置通知
     * @After 后置通知
     * @AfterReturning 返回通知
     * @AfterThrowinng 异常通知
     * @Around 环绕通知
     */
    static private String getFuncExp(JoinPoint joinPoint) {
        return joinPoint.getSignature().getName()+ "(" +
                Arrays.stream(joinPoint.getArgs()).map(String::valueOf).collect(Collectors.joining(","))
                + ")";
    }

    /**
     * 想在连接点之前执行
     */
    @Before("execution(public * com.bfh.aop.impl.MyMathCalculatorV2.*(int, *))")
    public static void logStart(JoinPoint joinPoint) {
        System.out.println("(Before) " + getFuncExp(joinPoint));
    }

    /**
     * 想在连接点之后执行
     * 切入点表达式写法
     *     固定格式：execution(访问权限符 返回值类型 方法全类名(参数表))
     *     通配符：
     *          *   1、匹配一个或多个字符：execution(public int com.xxx.MyMath*or.add(int, int))
     *              2、匹配任意一个传入参数
     *              3、只能匹配一层路径
     *              4、权限位置不能用 *，不写
     *
     *          ..  1、匹配任意数量，任意类型参数
     *              2、匹配任意多层路径：execution(public * com..MyMath*or.*(..)
     *
     *     最模糊：execution(* *.*(..))
     *     最精确：execution(public int com.bfh.aop.impl.MyMathCalculatorV2.add(int,int))
     *     支持 && 、 || 、!
     *          execution(public int com.bfh.MyMath*.*(..)) && execution(* *.*(int, int)) 同时满足两个表达式
     *          execution(public int com.bfh.MyMath*.*(..)) && execution(* *.*(int, int)) 满足任意一个
     *          !execution(public int com.bfh.MyMath*.*(..)) 除了指定表达式
     */
    @After("execution(public int com.bfh.aop.impl.MyMathCalculatorV2.*(int,int))")
    public static void logReturn(JoinPoint joinPoint) {
        System.out.println("(After) " + getFuncExp(joinPoint));
    }

    @AfterReturning(value = "execution(public int com.bfh.aop.impl.MyMathCalculatorV2.*(int,int))",returning = "result")
    public static void logEnd(JoinPoint joinPoint, Object result) {
        System.out.println("(AfterReturning) " + getFuncExp(joinPoint) + " = " + result);
    }

    /**
     * 想再连接点的目标代码执行异常时执行
     */
    @AfterThrowing(value = "execution(public int com.bfh.aop.impl.MyMathCalculatorV2.*(int,int))", throwing = "e")
    public static void logException(JoinPoint joinPoint, Exception e) {
        System.out.println("(AfterThrowing) " + getFuncExp(joinPoint) + " throw " + e);
    }

}
