package com.utils;

import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author benfeihu
 */
public class DateTimeUtilsTest {

    @Test
    public void formatLocalDate() {
        LocalDate localDate = LocalDate.of(2022, 3, 18);
        String r1 = DateTimeUtils.formatLocalDate(localDate, "yyyy-MM-dd");
        Assert.assertEquals("2022-03-18", r1);
        String r2 = DateTimeUtils.formatLocalDate(localDate, "yyyy-M-d");
        Assert.assertEquals("2022-3-18", r2);
        Assert.assertEquals("2022-03-18", localDate.format(DateTimeFormatter.ISO_DATE));
    }

    @Test
    public void formatLocalDateTime() {
        LocalDateTime localDateTime = LocalDateTime.of(2022, 3, 18, 11, 8, 3, 1234567);
        String r = DateTimeUtils.formatLocalDateTime(localDateTime, "yyyy-MM-dd HH:mm:ss.SSSSSS");
        Assert.assertEquals("2022-03-18 11:08:03.001234", r);
    }
}