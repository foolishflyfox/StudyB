
面向切面编程：将某段代码动态地切入到指定方法的指定位置进行运行的编程方式。

## 动态代理的使用

```java
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
                Object result = method.invoke(calculator, args);
                System.out.println(String.format("%s(%s) result = %s", method.getName(), strArgs, result));
                // 返回值必须返回回去，外界才能真正拿到结果
                return result;
            }
        };
        Class<?>[] interfaces = calculator.getClass().getInterfaces();
        ClassLoader loader = calculator.getClass().getClassLoader();

        // Proxy 为目标对象创建代理对象
        Object proxy = Proxy.newProxyInstance(loader, interfaces, h);
        return (Calculator) proxy;
    }
}
```
动态代理的问题：1、写起来难；2、jdk 默认的动态代理，如果目标对象没有实现任何接口，那么就不能用动态代理；

动态代理已经是面向切面了。Spring AOP 解决动态代理的痛点，AOP 的底层使用的就是 Java 的动态代理，可以一句代码都不写地去创建动态代理，实现简单，而且没有强制必须实现接口。

AOP 的专用术语：
1. 在横切关注点位置可以调用通知方法；
2. 通知方法收敛在切面类中；
3. 每个可以使用切面类通知方法可以插入的位置，称为连接点；
4. 真正需要执行通知方法的位置，称为切入点；
5. 切入点通过切入表达式选取；

环绕通知是优先于普通通知执行。

注解的优点：快速，简单；
配置的优点：1、功能完善，可以为其他类添加切面；2、重要的使用xml配置，不重要的使用注解；

AOP 的底层是动态代理。

IOC：
    1. ioc 是一个容器
    2. 容器启动的时候创建所有单实例对象
    3. 我们可以直接从容器中获取到这个对象；

SpringIOC：
    1. ioc 容器的启动过程？启动期间都做了什么(什么时候创建所有单实例bean)
    2. ioc 是如何创建呢这些单实例 bean，并如何管理的，到底保存在哪里？

思路：从 HelloWorld 开始，调试每个方法的作用。

1. ClassPathXMLApplicationContext
