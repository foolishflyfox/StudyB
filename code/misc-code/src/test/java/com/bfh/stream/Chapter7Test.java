package com.bfh.stream;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @author benfeihu
 */
public class Chapter7Test {
    @Test
    public void test7_1() {
        int n = 10;
        System.out.println(Stream.iterate(1, i -> i + 1).limit(n).parallel().reduce(0, Integer::sum));
        System.out.println(Stream.iterate(1, i -> i + 1).limit(n).parallel().reduce(Integer::sum));
        System.out.println(Stream.iterate(1, i -> i + 1).limit(n)
                .reduce("",
                        (s,v) -> {s += " ("+v+")"; return s;},
                        (s1, s2) -> "["+s1+"] [" + s2 + "]"));
        System.out.println(Stream.iterate(1, i -> i + 1).limit(n).parallel()
                .reduce("", (s,v) -> s += " ("+v+")", (s1, s2) -> "["+s1+"] [" + s2 + "]"));
        System.out.println(Stream.iterate(1, i -> i + 1).limit(n).parallel()
                .collect(Collectors.summingInt(Integer::valueOf)));
        System.out.println(Stream.iterate(1, i -> i+1).limit(n).parallel().mapToInt(v -> v).sum());
        System.out.println(IntStream.rangeClosed(1, n).sum());
        System.out.println(Stream.iterate(1, i -> i+1).limit(n).collect(() -> new StringBuffer(),
                (s, v) ->  s.append("("+v+")"),
                (s1, s2) -> s1.append(s2)
        ));

    }
    private Stream<Integer> getStream(int n) {
        return Stream.iterate(1, v -> v + 1).limit(n);
    }

    @Test
    public void test7_2() {
        int n = 10000;
        // 至于最后一个 sequential / parallel 设置生效
        System.out.println(getStream(n).parallel().map(v -> v*2).sequential().filter(v -> v%3==0).isParallel());
        // 只有一个元素的 set
        System.out.println(getStream(n)
                .parallel().map(v -> Thread.currentThread().getName())
                .sequential().map(String::toUpperCase).collect(Collectors.toSet()).size());
        // 有多个元素的 set，数量为计算机的核数
        System.out.println(getStream(n)
                .sequential().map(v -> Thread.currentThread().getName())
                .parallel().map(String::toUpperCase).collect(Collectors.toSet()).size());
    }
}
