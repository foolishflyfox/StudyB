package com.bfh.test;

import com.bfh.auto.Dog;
import com.bfh.auto.Family;
import com.bfh.config.AutoConfig;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author benfeihu
 */
public class AutoTest {
    @Test
    public void test01() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AutoConfig.class);
//        Dog bean = context.getBean(Dog.class);
//        Object cat = context.getBean("cat");
//        Family family = context.getBean(Family.class);
//        Assert.assertSame(bean, family.getDog());
    }
}
