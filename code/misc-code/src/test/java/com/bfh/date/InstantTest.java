package com.bfh.date;

import org.junit.Test;

import java.time.Instant;
import java.time.temporal.ChronoField;

/**
 * @author benfeihu
 */
public class InstantTest {
    @Test
    public void test() {
//        Instant instant = Instant.ofEpochSecond(3);
//        Instant instant = Instant.now();
//        System.out.println(instant.get(ChronoField.DAY_OF_MONTH));
        System.out.println(Instant.now().get(ChronoField.DAY_OF_MONTH));
    }
}
