package com.pdai.tech.annotation;

import com.utils.NetUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;

@Slf4j
public class RetentionTest {
    @SourcePolicy
    @SuppressWarnings("all")
    public void sourcePolicy() {}

    @ClassPolicy
    public void classPolicy() {}

    @RuntimePolicy
    public void runtimePolicy() {}

    static class Foo {
        public static void foo() {
            log.info("Foo.foo");
        }
    }

    @SneakyThrows
    @SuppressWarnings("magicnumber")
    public static void main(String[] args) {
        RetentionTest retentionTest = new RetentionTest();
        System.setProperty("local-ip", NetUtils.getLocalIp());
        Thread.currentThread().setName("MyMain");

        for (Annotation annotation : retentionTest.getClass().getMethod("sourcePolicy").getAnnotations()) {
            log.info("sourcePolicy annotation : " + annotation.getClass().getSimpleName());
        }

        for (Annotation annotation : retentionTest.getClass().getMethod("classPolicy").getAnnotations()) {
            log.info("classPolicy annotation : " + annotation.getClass().getSimpleName());
        }

        for (Annotation annotation : retentionTest.getClass().getMethod("runtimePolicy").getAnnotations()) {
            log.info("runtimePolicy annotation : " + annotation);
        }

        foo();
        Foo.foo();
    }
    public static void foo() {
        log.info("call foo");
    }
}
