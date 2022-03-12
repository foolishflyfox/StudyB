package com.pdai.tech.annotation;

import com.google.common.collect.Lists;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Optional;

public class RepeatAnnotationUseOldVersion {
    @interface Authority {
        String role();
    }
    @Retention(RetentionPolicy.RUNTIME)
    @interface Authorities {
        Authority[] value();
    }
    @Authorities({@Authority(role = "Admin"), @Authority(role = "Guest")})
    private static class TestClass {}

    public static void main(String[] args) {
        Optional.ofNullable(TestClass.class.getAnnotation(Authorities.class))
                .map(Authorities::value).map(Lists::newArrayList)
                .orElse(Lists.newArrayList())
                .forEach(System.out::println);
    }
}
