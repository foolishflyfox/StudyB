package com.bfh.mapper;

import lombok.*;
import org.dozer.DozerBeanMapper;
import org.junit.Test;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @author benfeihu
 */
public class DozerBeanMapperTest {
    @Test
    public void test01() {
        DozerBeanMapper dozer = new DozerBeanMapper();
        A a = new A(1234L, "2233", 234);
        B b = dozer.map(a, B.class);
        A a2 = dozer.map(a, A.class);
        System.out.println(b);
        System.out.println(a == a2);
        System.out.println(a);
        System.out.println(a2);
    }
    @Test
    public void test02() {
        DozerBeanMapper dozer = new DozerBeanMapper();
        InnerClassA innerClass = new InnerClassA(MyLocalDate.of(2021, 2, 2), "abc");
        InnerClassA map = dozer.map(innerClass, InnerClassA.class);
        System.out.println(map);
    }

    @Setter
    @AllArgsConstructor
     @NoArgsConstructor  // DozerBeanMapper 必须有无参构造函数，否则将抛出异常
    private static class MyLocalDate {
        private Integer year;
        private Integer month;
        private Integer day;
        public static MyLocalDate of(int year, int month, int day) {
            return new MyLocalDate(year, month, day);
        }
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class InnerClassA {
        MyLocalDate date;
        String s;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class InnerClassB {
        MyLocalDate date;
        String s;
    }
}
