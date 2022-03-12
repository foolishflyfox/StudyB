package com.bfh.xmlaop;

/**
 * @author benfeihu
 */
public class A {
    public void foo() {
        System.out.println("A foo()");
    }

    public Integer add(Integer a, Integer b) {
        Integer c = a+b;
        System.out.println("A add(" + a + ", " + b + ")=" + c);
        return c;
    }
}
