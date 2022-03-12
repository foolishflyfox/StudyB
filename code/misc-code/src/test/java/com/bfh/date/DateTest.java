package com.bfh.date;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author benfeihu
 */
public class DateTest {
    @Test
    public void test01() {
        Date now = new Date();
        System.out.println(now);
        // 2014/03/18
        Date d1 = new Date(114, 2, 18);
        System.out.println(d1);
    }

    @Test
    public void test02() {
        System.out.println(new SimpleDateFormat("yy-MM-dd HH-mm").format(new Date()));
    }

    @Test
    public void test03() {
        SimpleDateFormat headerDateFormat = new SimpleDateFormat("M/d");
        System.out.println(headerDateFormat.format(new Date(121, 10,12)));
    }
}
