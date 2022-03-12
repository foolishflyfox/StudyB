package com.bfh.time;

import com.google.common.collect.Maps;
import org.junit.Test;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;

/**
 * @author benfeihu
 */
public class TimeTest {

    @Test
    public void test01() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = sdf.parse("2022-01-24 23:03:30");
        System.out.println(ZoneId.systemDefault());
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(date.getTime()),
                ZoneId.systemDefault());
        LocalDateTime startOfDay = localDateTime.with(LocalTime.MIN);
        System.out.println(Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant()));
    }

    @Test
    public void test02() {
        SimpleDateFormat sdf = new SimpleDateFormat("y/M/d");
        System.out.println(sdf.format(new Date()));
        Map<Integer, Integer> map = Maps.newHashMap();
        System.out.println(map.get(1));
    }

    @Test
    public void test03() {
        Integer v = null;
        System.out.println("xx" + v);
    }

}
