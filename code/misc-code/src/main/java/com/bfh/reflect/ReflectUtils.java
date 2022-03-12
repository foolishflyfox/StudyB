package com.bfh.reflect;

import lombok.SneakyThrows;

import java.lang.reflect.Method;

/**
 * @author benfeihu
 */
public class ReflectUtils {
    @SneakyThrows
    public static Object invokeMethod(Object object, String methodName) {
        Class clazz = object.getClass();
        Method method = clazz.getDeclaredMethod(methodName);
        method.setAccessible(true);
        return method.invoke(object);
    }
}
