package com.bfh.date;

import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;

/**
 * @author benfeihu
 */
public class LocalDateTimeTest {
    @Test
    public void test01() {
        LocalDate date = LocalDate.of(2014, 3, 18);
        LocalTime time = LocalTime.of(13,45,2);
        LocalDateTime dt1 = LocalDateTime.of(2014, Month.MARCH, 18, 13, 45, 2);
        System.out.println(dt1);
        LocalDateTime dt2 = LocalDateTime.of(date, time);
        System.out.println(dt2);
        System.out.println(date.atTime(13,45,2));
        System.out.println(date.atTime(time));
        System.out.println(time.atDate(date));
        System.out.println(dt1.toLocalDate());
        System.out.println(dt1.toLocalTime());
    }
}
