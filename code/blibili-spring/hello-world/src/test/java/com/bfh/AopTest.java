package com.bfh;

import com.bfh.aop.Calculator;
import com.bfh.aop.impl.MyMathCalculator;
import com.bfh.aop.impl.MyMathCalculatorV2;
import com.bfh.aop.proxy.CalculatorProxy;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author benfeihu
 */
public class AopTest {

    @Test
    public void test() {
        Calculator calculator = new MyMathCalculator();
        Calculator proxy = CalculatorProxy.getProxy(calculator);
        System.out.println(proxy.add(10, 3));
//        System.out.println(proxy.div(10, 0));
        proxy.div(10, 1);
        System.out.println(proxy.mul(3, 21));
    }

    @Test
    public void testAop() {
        ApplicationContext context = new ClassPathXmlApplicationContext("aop1.xml");
        // 如果想要用类型，一定要用接口类型，不要用它本类
        Calculator myMathCalculator = context.getBean(Calculator.class);
        myMathCalculator.add(1,2);
        System.out.println(myMathCalculator);  // com.bfh.aop.impl.MyMathCalculator@785af90c
        System.out.println(myMathCalculator.getClass());  // class com.sun.proxy.$Proxy17
        Calculator bean = context.getBean("myMathCalculator", Calculator.class);
        bean.mul(3, 4);
    }

    @Test
    public void testAop2() {
        ApplicationContext context = new ClassPathXmlApplicationContext("aop1.xml");
//        MyMathCalculatorV2 myMathCalculatorV2 = context.getBean(MyMathCalculatorV2.class);
//        myMathCalculatorV2.add(1,2);
//        System.out.println(myMathCalculatorV2);  // com.bfh.aop.impl.MyMathCalculatorV2@5d00865
//        System.out.println(myMathCalculatorV2.getClass());  // class com.bfh.aop.impl.MyMathCalculatorV2$$EnhancerByCGLIB$$2ba6a1f1
//        System.out.println(myMathCalculatorV2.getClass().getSuperclass());  // class com.bfh.aop.impl.MyMathCalculatorV2
        // spring4 无异常的顺序为：@Before  -- @After  --  @AfterReturning
        MyMathCalculatorV2 myMathCalculatorV2 = context.getBean(MyMathCalculatorV2.class);
//        myMathCalculatorV2.add(1,2);
        // spring4 抛异常的顺序为：@Before  -- @After  --  @AfterThrowing
        myMathCalculatorV2.add(1, 1);
        myMathCalculatorV2.div(1,0);
    }
}
