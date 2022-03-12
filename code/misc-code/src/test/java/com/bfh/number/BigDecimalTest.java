package com.bfh.number;

import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author benfeihu
 */
public class BigDecimalTest {
    @Test
    public void test01() {
        BigDecimal v1 = new BigDecimal(1L);
        BigDecimal v2 = new BigDecimal(3L);
//        v1.divide(v2);
        System.out.println(v1.divide(v2, 5, RoundingMode.HALF_UP));
    }
}
