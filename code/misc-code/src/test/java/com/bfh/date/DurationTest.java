package com.bfh.date;

import org.junit.Test;

import java.time.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author benfeihu
 */
public class DurationTest {
    @Test
    public void test01() {
        LocalTime t1 = LocalTime.of(2, 10, 8);
        LocalTime t2 = LocalTime.of(4, 2, 32);
        LocalDate d1 = LocalDate.of(2022, 2, 5);
        LocalDate d2 = LocalDate.of(2022, 2, 10);
        LocalDateTime dt1 = d1.atTime(t1);
        LocalDateTime dt2 = d2.atTime(t2);
        Duration duration1 = Duration.between(t2, t1);
        Duration duration2 = Duration.between(dt1, dt2);
        Period period1 = Period.between(d1, d2);
        System.out.println(duration1.getSeconds());
        System.out.println(duration2.getSeconds());
        System.out.println(period1.getMonths());
    }
}
