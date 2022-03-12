package com.bfh.test;

import com.bfh.config.MainConfigLifeCycle;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author benfeihu
 */
public class BeanLifeCycleTest {
    @Test
    public void test01() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MainConfigLifeCycle.class);
        System.out.println("容器创建完成...");
//        Car bean = context.getBean(Car.class);
        context.close();
    }
}
