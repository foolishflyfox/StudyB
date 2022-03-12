package com.bfh.date;

import org.junit.Test;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalField;

/**
 * @author benfeihu
 */
public class LocalTimeTest {
    @Test
    public void test01() {
        // 创建 LoaclTime 并读取其值
        LocalTime time = LocalTime.of(12,45,20);
        System.out.println("hore = " + time.getHour());
        System.out.println("minute = " + time.get(ChronoField.MINUTE_OF_HOUR));
        System.out.println("second = " + time.getSecond());
    }
    @Test
    public void test02() {
        LocalTime t1 = LocalTime.parse("12:42:02");
        System.out.println(t1);
        LocalTime t2 = LocalTime.parse("12:2:02", DateTimeFormatter.ofPattern("HH:m:s"));
        System.out.println(t2);

    }
}
