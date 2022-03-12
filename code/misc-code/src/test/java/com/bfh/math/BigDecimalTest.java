package com.bfh.math;

import org.junit.Test;

import java.math.BigDecimal;

/**
 * @author benfeihu
 */
public class BigDecimalTest {
    @Test
    public void test01() {
        long value = 50000L;
        BigDecimal tmp =BigDecimal.valueOf(value).multiply(BigDecimal.valueOf(60000L));
        BigDecimal r1 = tmp.divide(BigDecimal.valueOf(60000L*4), BigDecimal.ROUND_FLOOR);
        System.out.println(r1.longValue() * 4);
    }
}
