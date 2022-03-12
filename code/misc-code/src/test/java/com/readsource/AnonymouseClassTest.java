package com.readsource;

import org.junit.Assert;
import org.junit.Test;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author benfeihu
 */
public class AnonymouseClassTest {
    @Test
    public void testInnerThisType () {
        Supplier<String> supplier1 = new Supplier<String>() {
            @Override
            public String get() {
                return this.getClass().getName();
            }
        };
        Supplier<String> supplier2 = () -> {
            return this.getClass().getName();
        };
        // 匿名函数的 this 是匿名类实例
        Assert.assertNotEquals(this.getClass().getName(), supplier1.get());
        // lambda 表达式中使用的 this 是所在类的实例
        Assert.assertEquals(this.getClass().getName(), supplier2.get());
    }
}
