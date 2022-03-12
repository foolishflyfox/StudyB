package com.bfh.collection;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author benfeihu
 */
public class MapTest {
    @Test
    public void test01() {
        Map<Integer, String> map = ImmutableMap.of(1, "a", 2, "b", 3, "c");
        map.forEach((k,v) -> System.out.println(String.format("map[%d] = %s", k, v)));
    }
    @Test
    public void test02() {
        Map<String, Integer> map = new HashMap<>();
        System.out.println(map.get("a"));
    }
}
