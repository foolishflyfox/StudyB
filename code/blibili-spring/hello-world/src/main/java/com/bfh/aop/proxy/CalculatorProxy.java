package com.bfh.aop.proxy;

import com.bfh.aop.Calculator;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * 帮 Calculator.java 生成代理对象的类
 * @author benfeihu
 */
public class CalculatorProxy {

    /**
     * 为传入的参数对象创建一个动态代理对象
     * @param calculator 被代理对象
     * @return 代理对象
     */
    public static Calculator getProxy(final Calculator calculator) {
        // 执行器，帮我们的目标对象执行目标方法
        InvocationHandler h = new InvocationHandler() {
            /**
             * @param proxy，代理对象
             * @param method 当前要执行的目标对象的方法
             * @param args
             * @return
             * @throws Throwable
             */
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                String strArgs = Arrays.stream(args).map(String::valueOf).collect(Collectors.joining(","));
                System.out.println(String.format("%s(%s) start", method.getName(), strArgs));
                // 利用反射执行目标方法
                // 目标方法执行后的返回值
                Object result = null;
                try {
                    result = method.invoke(calculator, args);
                } catch (Exception e) {
                    System.out.println("发生异常：" + e.getCause());
                }
                System.out.println(String.format("%s(%s) result = %s", method.getName(), strArgs, result));
                // 返回值必须返回回去，外界才能真正拿到结果
                return result;
            }
        };
        Class<?>[] interfaces = calculator.getClass().getInterfaces();
        ClassLoader loader = calculator.getClass().getClassLoader();

        // Proxy 为目标对象创建代理对象，也实现了 Calculator 接口
        Object proxy = Proxy.newProxyInstance(loader, interfaces, h);
        System.out.println(Arrays.toString(proxy.getClass().getInterfaces()));
        return (Calculator) proxy;
    }
}
