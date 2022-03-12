## 注解基础

注解是 JDK1.5 版本开始引入的一个特性，用来对代码进行说明，可以对包、类、接口、字段、方法参数、局部变量等进行注解。它主要的作用有以下四个方面：
- 生成文档，通过代码中标识的元数据生成 javadoc 文档；
- 编译检查，通过代码中的元数据让编译器在编译期间进行检查验证；
- 编译时动态处理，编译时通过代码中标识的元数据动态处理，例如动态生成代码；
- 运行时动态处理，运行时通过代码中标识的元数据动态处理，例如使用反射注入实例；

注解的常见分类：
- java 自带的标准注解：@Override / @Decprecated / @SuppressWarnings ，分别用于注明重写某个方法、标明某个类或方法过时、标明要忽略的警告，用这些注解标明后编译器就会进行检查。
- 元注解：元注解是用于定义注解的注解，包括：@Retention / @Target / @Inherited / @Document。@Retention 用于标明注解被保留的阶段，@Target 用于标明注解作用的范围，@Inherited 用于标明注解可继承，@Documented 用于标明是否生成 javadoc 文档。
- 自定义注解，可以根据自己的需求定义注解，并可用元注解对自定义注解进行注解。

## Java 内置注解

### SuppressWarnings

可取的参数：
- all: 抑制所有告警
- rawtype: 使用 generics 时，忽略没有指定相应的类型；
- unchecked
- magicnumber

## 元注解 

### @Target

Target 的作用是描述注解的使用范围。其参数为 ElementType 类型。

```java
public enum ElementType {
    TYPE,  // 类、接口、枚举类
    FIELD,  // 成员变量(包括: 枚举常量)
    METHOD,  // 成员方法
    PARAMETER,  // 函数形参
    CONSTRUCTOR,  // 构造函数
    LOCAL_VARIABLE,  // 局部变量
    ANNOTATION_TYPE,  // 注解类
    PACKAGE,  // 包
    TYPE_PARAMETER,  // 类型参数，JDK1.8 新增
    TYPE_USE;  // 使用类型的任何地方

    private ElementType() {
    }
}
```

### @Retention

```java
public enum RetentionPolicy {
    SOURCE,
    CLASS,
    RUNTIME;

    private RetentionPolicy() {
    }
}
```

### @Inherited

Inherited 注解的作用：被它修饰的 Annotation 将具有继承性。如果某个类使用了被 @Inherited 修饰的 Annotation，则其子类将自动具有该注解。

```java
package com.bfh.bean;

import java.lang.annotation.*;

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
            System.out.println(annotation.toString());
        }
    }
}
```
输出结果为：`@com.pdai.tech.annotation.InheritedDemo$Foo(values=[a, bb], number=12)`。

即使 Student 类没有显式地注解 `Foo`，但是它的父类 Person 被注解，而且 `@Foo` 被 `@Inherited` 注解，因此 Student 类自动有了该注解。

### @Repeatable

允许在同一声明类型多次使用同一个注解。

在 java 8 之前，也有重复注解的解决方案，但是可读性不太好，比如下面的代码。
```java
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
```
执行结果为：
```java
@com.pdai.tech.annotation.RepeatAnnotationUseOldVersion$Authority(role=Admin)
@com.pdai.tech.annotation.RepeatAnnotationUseOldVersion$Authority(role=Guest)
```
由另一个注解存储重复注解，在使用时候，用存储注解 Authorities 来扩展重复注解。

再来看看 Java 8 中的做法。
```java
import com.google.common.collect.Lists;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Optional;

public class RepeatAnnotationUseNewVersion {

    @Repeatable(Authorities.class)
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
        Optional.ofNullable(TestClass.class.getAnnotation(Authorities.class))
                .map(Authorities::value).map(Lists::newArrayList)
                .orElse(Lists.newArrayList())
                .forEach(System.out::println);
    }
}
```
执行结果为：
```
@com.pdai.tech.annotation.RepeatAnnotationUseNewVersion$Authority(role=Admin)
@com.pdai.tech.annotation.RepeatAnnotationUseNewVersion$Authority(role=Guest)
```

## 注解与反射接口

AnnotatedElement 接口是所有程序元素(Class/Method/Field/Constructor)的父接口，所以程序通过反射获取了某个类的 AnnotatedElement 对象后，程序就可以调用该对象的方法来访问 Annotation 信息。

相关的接口：
- `boolean isAnnotationPresent(Class<? extends Annotation> annotationClass)`: 判断该程序元素上是否包含指定类型的注解；注意，此方法会忽略注解对应的注解容器；
- `<T extends Annotation> T getAnnotation(Class<T> annotationClass)`: 返回该程序元素上存在的、指定类型的注解，如果该类型注解不存在，返回 null；
- `Annotation[] getAnnotations()`: 返回该程序元素上存在的所有注解，如果没有注解，返回长度为0的数组；忽略容器中的注解；
- `<T extends Annotation> T[] getAnnotationsByType(Class<T> annotationClass)`: 返回该程序元素上存在的、指定类型的注解数组。与 getAnnotation 的区别在于，getAnnotationsByType 会检测注解对应的重复注解容器。若程序元素为类，当前类上找不到注解，且该注解可继承，则会去父类上检测对应的注解。
- `<A extends Annotation> A getDeclaredAnnotation(Class<A> annotationClass)`: 返回直接存在于此元素上的指定类型注解。与此接口中的其他方法不同，该方法忽略继承的注解；也忽略注解容器中的注解；
- `Annotation[] getDeclaredAnnotations()`: 返回所有注解，忽略继承注解和容器中的注解；
- `<A extends Annotation> A[] getDeclaredAnnotationsByType(Class<A> annotationClass)`: 返回指定类型注解，忽略继承，不忽略容器中的注解；


