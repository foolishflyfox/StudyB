package com.bfh.date;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author benfeihu
 */
public class LocalDateTest {
    @Test
    public void test01() {
        // 创建一个 LocalDate 对象并读取其值
        LocalDate date = LocalDate.of(2014, 3, 18);
        System.out.println("year = " + date.getYear());
        System.out.println("month = " + date.getMonth());
        System.out.println("day = " + date.getDayOfMonth());
        System.out.println("dow = " + date.getDayOfWeek());
        System.out.println("len = " + date.lengthOfMonth());
        System.out.println("leap = " + date.isLeapYear());  // 是否为闰月
    }
    @Test
    public void test02() {
        // 创建一个 LocalDate 对象并读取其值
        LocalDate date = LocalDate.of(2014, 3, 18);
        System.out.println("year = " + date.get(ChronoField.YEAR));
        System.out.println("month = " + date.get(ChronoField.MONTH_OF_YEAR));
        System.out.println("day = " + date.get(ChronoField.DAY_OF_MONTH));
        System.out.println("dow = " + date.get(ChronoField.DAY_OF_WEEK));
    }
    @Test
    public void test03() {
        // 获取当前时间
        LocalDate now = LocalDate.now();
        System.out.println(now);
    }
    @Test
    public void test04() {
        // 解析字符串
        LocalDate date1 = LocalDate.parse("2014-03-18");
        System.out.println(date1);
        LocalDate date2 = LocalDate.parse("2014/3/8", DateTimeFormatter.ofPattern("y/M/d"));
        System.out.println(date2);
    }

    @Test
    public void test05() {
        LocalDate today = LocalDate.now();
        System.out.println(today);
        // 添加一天
        LocalDate tomorrow = today.plusDays(1);
        System.out.println(tomorrow);
        // 月初
        LocalDate first = today.withDayOfMonth(1);
        System.out.println(first);
        // 月末
        LocalDate end = today.withDayOfMonth(today.lengthOfMonth());
        System.out.println(end);
        System.out.println(today.getDayOfMonth());
    }

    @Test
    public void test06() {
        LocalDate today = LocalDate.now();
        System.out.println(Date.from(today.atTime(23, 0, 0).atZone(ZoneId.systemDefault()).toInstant()));
    }

    public static Date getEndDateOfCurrentMonth(Date date) {
        LocalDateTime localDate = Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime endOfMonth = localDate.with(TemporalAdjusters.lastDayOfMonth());
        return Date.from(endOfMonth.atZone(ZoneId.systemDefault()).toInstant());
    }

    @Test
    public void test07() {
        LocalDate today = LocalDate.now();
        List<LocalDate> dts = Lists.newArrayList();
        List<String> ss = Lists.newArrayList();
        SimpleDateFormat sdf = new SimpleDateFormat("M/d");
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("M/d");
        for (LocalDate dt = today.withDayOfMonth(1); !dt.isAfter(today); dt =dt.plusDays(1)) {
            dts.add(dt);
//            ss.add(dt.format(dtf));
            ss.add(dtf.format(dt));
        }
        System.out.println(dts);
        System.out.println(ss);
        System.out.println(dts.stream().map(dtf::format).collect(Collectors.toList()));
    }

    @Test
    public void test08() {
        LocalDate d1 = LocalDate.now();
        LocalDate d2 = LocalDate.of(2022, 2, 13);
        System.out.println(d1);
        System.out.println(d2);
        System.out.println(d1==d2);
        System.out.println(d1.equals(d2));
        Map<LocalDate, Integer> map = Maps.newHashMap();
        map.put(d1, 1);
        map.put(d2, 2);
        System.out.println(map);
    }

    @Test
    public void test09() {
        LocalDate d1 = LocalDate.of(2021, 1,2);
        LocalDate d2 = LocalDate.of(2020, 12,31);
        System.out.println(Period.between(d2, d1).getDays());
        System.out.println(Period.between(d1, d2).getDays());
        System.out.println(d1.getMonthValue());
        System.out.println(getClass().getSimpleName());
    }

    @Test
    public void test10() {
        LocalDate d1 = LocalDate.now();
        LocalDate d2 = DateTimeUtils.getLocalDate(new Date());
        System.out.println(d1);
        System.out.println(d2);
        System.out.println(Objects.equals(d1, d2));
    }

    @Test
    public void test11() {
        System.out.println(LocalDate.now());
    }

    @Test
    public void test12() {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        System.out.println(today);
    }

}
