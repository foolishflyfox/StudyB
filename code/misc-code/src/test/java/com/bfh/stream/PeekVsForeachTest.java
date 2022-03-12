package com.bfh.stream;

import org.junit.Test;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author benfeihu
 */
public class PeekVsForeachTest {

    @Test
    public void test() {
        // 终端操作，有输出
        Stream.of("a", "b", "c").map(String::toUpperCase).forEach(System.out::println);
        System.out.println("====");
        // 非终端操作，无输出
        Stream.of("a", "b", "c").map(String::toUpperCase).peek(System.out::println);
    }

}
