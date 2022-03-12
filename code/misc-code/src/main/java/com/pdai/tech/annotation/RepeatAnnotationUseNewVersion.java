package com.pdai.tech.annotation;

import com.google.common.collect.Lists;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;
import java.util.Optional;

public class RepeatAnnotationUseNewVersion {

    @Repeatable(Authorities.class)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Authority {
        String role();
    }
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Authorities {
        Authority[] value();
    }
    @Authority(role = "Admin")
    @Authority(role = "Guest")
    private static class TestClass {}

    public static void main(String[] args) {
        System.out.println(Arrays.toString(TestClass.class.getAnnotations()));
        Optional.ofNullable(TestClass.class.getAnnotation(Authorities.class))
                .map(Authorities::value).map(Lists::newArrayList)
                .orElse(Lists.newArrayList())
                .forEach(System.out::println);
        System.out.println(TestClass.class.isAnnotationPresent(Authority.class));
        System.out.println(TestClass.class.getAnnotation(Authority.class));  // null
        System.out.println(Arrays.toString(TestClass.class.getAnnotationsByType(Authority.class)));  // 数组为2
        System.out.println(TestClass.class.getDeclaredAnnotation(Authority.class));
        System.out.println(Arrays.toString(TestClass.class.getDeclaredAnnotations()));
        System.out.println(Arrays.toString(TestClass.class.getDeclaredAnnotationsByType(Authority.class)));
    }
}
