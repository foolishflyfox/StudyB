package com.readsource;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author benfeihu
 */
public class ConstructTest {

    static class A {
        public A() {
            System.out.println("A construct");
        }
    }

    @Test
    public void testNewInstance() throws Exception {
        A a1 = new A();
        A a2 = A.class.newInstance();
        Assert.assertTrue(a2 instanceof A);
    }

}
