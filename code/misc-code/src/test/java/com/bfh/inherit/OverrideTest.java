package com.bfh.inherit;

import org.junit.Test;

public class OverrideTest {
    static class A {
        private void foo() {
            System.out.println("A foo");
        }
    }
    static class B extends A {
        void foo() {
            System.out.println("B foo");
        }
    }

    @Test
    public void test01() {
        A a = new B();
        a.foo(); // A foo
    }
}
