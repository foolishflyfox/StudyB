package com.bfh.misc;

/**
 * @author benfeihu
 */
public class UseNormalClass {
    public static void foo() {
        String s = NormalClass.ss;  // class 中该行的内容变为 String s = "hello, world";
        System.out.println(s);
    }
}
