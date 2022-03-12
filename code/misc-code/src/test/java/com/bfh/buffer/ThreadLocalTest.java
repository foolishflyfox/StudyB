package com.bfh.buffer;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.Test;

/**
 * @author benfeihu
 */
public class ThreadLocalTest {

    @Data
    @AllArgsConstructor
    private static class User {
        private String name;
        private Integer age;
    }

    ThreadLocal<User> userThreadLocal = new ThreadLocal<>();

    public void foo() {
        System.out.println("foo : name = " + userThreadLocal.get().getName());
    }

    public void bar() {
        System.out.println("bar : age = " + userThreadLocal.get().getAge());
    }

    @Test
    public void test1() {
        try {
            userThreadLocal.set(new User("张三", 22));
            foo();
            bar();
        } catch (Exception e) {
            userThreadLocal.remove();
        }

    }

}
