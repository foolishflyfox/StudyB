package com.bfh;

import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

/**
 * @author benfeihu
 */
public class NormalTest {
    @Test
    public void t1() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        System.out.println(calendar.get(Calendar.DATE));
        calendar.set(Calendar.DATE, -1);
        System.out.println(calendar.get(Calendar.DATE));
    }
}
