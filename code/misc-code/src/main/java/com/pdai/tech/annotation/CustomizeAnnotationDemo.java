package com.pdai.tech.annotation;

import java.io.FileNotFoundException;
import java.lang.annotation.*;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class CustomizeAnnotationDemo {
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @interface MyMethodAnnotation {
        String title() default "";
        String description() default "";
    }

    @Override
    @MyMethodAnnotation(title = "toStringMethod", description = "override toString method")
    public String toString() {
        return this.getClass().getSimpleName() + "::toString";
    }
    @Deprecated
    @MyMethodAnnotation(title = "old static method", description = "deprecated old method")
    public static void oldMethod() {
        System.out.println("old method, don't use it!");
    }
    @SuppressWarnings({"unchecked", "deprecation"})
    @MyMethodAnnotation(title = "test method", description = "suppress warning static method")
    public static void genericsTest() throws FileNotFoundException {
        List l = new ArrayList();
        l.add("abc");
        oldMethod();
    }

    public static void main(String[] args) {
        try {
            Method[] methods = CustomizeAnnotationDemo.class.getMethods();
            for (Method method : methods) {
                if (method.isAnnotationPresent(MyMethodAnnotation.class)) {
                    for (Annotation anno : method.getDeclaredAnnotations()) {
                        System.out.println("Annotation in Method " + method + " : " + anno);
                    }
                    MyMethodAnnotation methodAnno = method.getAnnotation(MyMethodAnnotation.class);
                    System.out.println(methodAnno.title());
                }
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }
}
