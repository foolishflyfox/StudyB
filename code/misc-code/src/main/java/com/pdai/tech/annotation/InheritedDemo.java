package com.pdai.tech.annotation;

import java.lang.annotation.*;
import java.util.Arrays;

public class InheritedDemo {
    @Inherited
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE, ElementType.METHOD})
    @interface Foo {
        String[] values();
        int number();
    }

    @Foo(values = {"a", "bb"}, number = 12)
    private static class Person {}

    private static class Student extends Person {}

    public static void main(String[] args) {
        Class<Student> clazz = Student.class;
        Annotation[] annotations = clazz.getAnnotations();
        for (Annotation annotation : annotations) {
            System.out.println("getAnnotations: " + annotation.toString());
        }
        System.out.println("getAnnotation: " + clazz.getAnnotation(Foo.class));
        System.out.println("getAnnotationsByType: " + Arrays.toString(clazz.getAnnotationsByType(Foo.class)));
        System.out.println("getDeclaredAnnotation: " + clazz.getDeclaredAnnotation(Foo.class));
    }
}

