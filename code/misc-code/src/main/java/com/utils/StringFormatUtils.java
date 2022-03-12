package com.utils;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class StringFormatUtils {
    public static String hexString(byte[] data, String delimiter) {
        return IntStream.range(0, data.length).map(i -> data[i]).boxed()
                .map(v -> String.format("%02x", v & 0xff))
                .collect(Collectors.joining(delimiter));
    }
    public static String hexString(byte[] data) {
        return hexString(data, " ");
    }
}
