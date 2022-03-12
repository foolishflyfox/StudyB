package com.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author benfeihu
 */
public class ReflectUtils {
    public static <T> T getPrivateFieldOfObj(Object obj, String fieldName, Class<T> clazz) {
        try {
            Field declaredField = obj.getClass().getDeclaredField(fieldName);
            declaredField.setAccessible(true);
            @SuppressWarnings("unchecked")
            T result = (T) declaredField.get(obj);
            return result;
        } catch (Exception e) {
            return null;
        }
    }
    public static String getMethodSignature(Method method) {
        return Modifier.toString(method.getModifiers()) + " "
                + method.getReturnType().getSimpleName() + " "
                + method.getName() + "("
                + Arrays.stream(method.getParameters()).map(Parameter::getType)
                .map(Class::getSimpleName).collect(Collectors.joining(", "))
                + ")";
    }
    public static String getFieldSignature(Field field) {
        return Modifier.toString(field.getModifiers()) + " "
                + field.getType().getSimpleName() + " "
                + field.getName();
    }
}
