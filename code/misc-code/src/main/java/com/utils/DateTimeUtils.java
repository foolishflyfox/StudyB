package com.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author benfeihu
 */
public class DateTimeUtils {
    public static void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将 localDate 根据格式转换为字符串
     * @param localDate 待转换的日期
     * @param format 字符串格式，例如yyyy-MM-dd / yyyy-M-d
     * @return
     */
    public static String formatLocalDate(LocalDate localDate, String format) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(format);
        return localDate.format(dateTimeFormatter);
    }

    /**
     * 将 localDateTime 根据格式转换为字符串
     * @param localDateTime 待转换的日期
     * @param format 字符串格式，例如yyyy-MM-dd / yyyy-M-d
     * @return
     */
    public static String formatLocalDateTime(LocalDateTime localDateTime, String format) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(format);
        return localDateTime.format(dateTimeFormatter);
    }
}
