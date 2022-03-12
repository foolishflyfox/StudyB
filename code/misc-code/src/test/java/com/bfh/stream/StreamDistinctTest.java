package com.bfh.stream;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author benfeihu
 */
public class StreamDistinctTest {
    @Test
    public void test01() {
        System.out.println(Stream.of(1, 6, 3, 2, 1, 4, 6).distinct().collect(Collectors.toList()));
        String s1 = "ejiate，erhu";
        String s2 = "sss";
        String[] split = s1.split("，");
        System.out.println(Arrays.toString(s1.split("，")));
        System.out.println(Arrays.toString(s2.split("，")));
        Set<Integer> set1 = Sets.newHashSet(1,2,3);
        Set<Integer> set2 = Sets.newHashSet(1,2);
        System.out.println(set1 == set2);
        Map<Set<Integer>, String> map = new HashMap<>();
        map.put(set1, "aaa");
        map.put(set2, "bbb");
        map.put(null, "ccc");
        System.out.println(map);
    }
}
