package com.bfh;

import com.bfh.xmlaop.A;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author benfeihu
 */
public class XmlAopTest {

    @Test
    public void t1() {
        ApplicationContext context = new ClassPathXmlApplicationContext("xmlaop.xml");
        A a = context.getBean(A.class);
        a.foo();
        a.add(2,5);
    }

}
