package com.readsource;

import com.utils.ReflectUtils;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflectTest {
    static class A {
        private Integer privateAField;
        protected Integer protectedAField;
        public Integer publicAField;
        private void privateAMethod() {}
        protected void protectedAMethod() {}
        public void publicAMethod() {}
        public static void publicAStaticMethod() {}
        protected static void protectedAStaticMethod() {}
        private static void privateAStaticMethod() {}
        public static Integer publicAStaticField;
        protected static Integer protectedAStaticField;
        private static Integer privateAStaticField;
    }
    static class B extends A {
        private Integer privateBField;
        protected Integer protectedBField;
        public Integer publicBField;
        private void privateBMethod() {}
        protected void protectedBMethod() {}
        public void publicBMethod() {}
        public static void publicBStaticMethod() {}
        protected static void protectedBStaticMethod() {}
        private static void privateBStaticMethod() {}
        public static Integer publicBStaticField;
        protected static Integer protectedBStaticField;
        private static Integer privateBStaticField;
    }
    @Test
    public void testGetMothod() {
        try {
            B.class.getMethod("publicAMethod");
            B.class.getMethod("publicAStaticMethod");
        } catch (NoSuchMethodException e) {
            Assert.fail();  // 可以获取父类的 public 方法，不会抛错
        }
        try {
            B.class.getMethod("protectedAMethod");
        } catch (Exception e) {
            // // 可以获取父类的 protected 方法，不会抛错
            Assert.assertTrue(e instanceof NoSuchMethodException);
        }
    }

    @Test
    public void testGetMethods() {
        /*
         * 输出如下，表示 getMethods 会获取所有的 public 类型的方法，包括父类、静态
            public void publicBMethod()
            public static void publicBStaticMethod()
            public void publicAMethod()
            public static void publicAStaticMethod()
            public final void wait(long, int)
            public final native void wait(long)
            public final void wait()
            public boolean equals(Object)
            public String toString()
            public native int hashCode()
            public final native Class getClass()
            public final native void notify()
            public final native void notifyAll()
         */
        for (Method method : B.class.getMethods()) {
            System.out.println(ReflectUtils.getMethodSignature(method));
        }
    }
    @Test
    public void testGetDeclaredMethods() {
        /**
         * 输出如下：表示 getDeclaredMethods 会获取所有本类中声明的方法，包括静态、私有
         * private void privateBMethod()
         * protected void protectedBMethod()
         * public void publicBMethod()
         * public static void publicBStaticMethod()
         * protected static void protectedBStaticMethod()
         * private static void privateBStaticMethod()
         */
        for (Method method : B.class.getDeclaredMethods()) {
            System.out.println(ReflectUtils.getMethodSignature(method));
        }
    }
    @Test
    public void testGetFields() {
        /**
         * 输出如下：表示 getFields 会获取所有的 public 字段，包括父类和静态
         * public Integer publicBField
         * public static Integer publicBStaticField
         * public Integer publicAField
         * public static Integer publicAStaticField
         */
        for (Field field : B.class.getFields()) {
            System.out.println(ReflectUtils.getFieldSignature(field));
        }
    }
    @Test
    public void testGetDeclaredFields() {
        /**
         * 输出如下：表示 getDeclaredFields 会获取本类声明的字段，包括 private、static
         * private Integer privateBField
         * protected Integer protectedBField
         * public Integer publicBField
         * public static Integer publicBStaticField
         * protected static Integer protectedBStaticField
         * private static Integer privateBStaticField
         */
        for (Field field : B.class.getDeclaredFields()) {
            System.out.println(ReflectUtils.getFieldSignature(field));
        }
    }
}
