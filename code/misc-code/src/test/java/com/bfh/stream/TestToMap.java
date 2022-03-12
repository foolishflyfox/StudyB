package com.bfh.stream;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.collections.ListUtils;
import org.junit.Test;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author benfeihu
 */
public class TestToMap {
    @Test
    public void test01() {
        Map<Integer, String> m = new HashMap() {{
            put(1, "one");
            put(2, "two");
        }};
        BinaryOperator<String> foo = (s1, s2) -> {
            return s1;
        };
        Map<Integer, String> r = Stream.of(1, 3, 3)
                .collect(Collectors.toMap(Function.identity(), v -> m.get(v), foo));
        System.out.println(r);
    }
    @Test
    public void test02() {
        List<A> list = Lists.newArrayList(new A(1, 2), new A(1, 0), new A(1,3), new A(2, 2));
        System.out.println(list.stream().collect(Collectors.<A, Integer, List<Integer>>toMap(A::getK,
                a -> a.getV()==0? Collections.emptyList():Lists.newArrayList(a.getV()),
                ListUtils::union)));
    }

    @Data
    @AllArgsConstructor
    private class A {
        private Integer k;
        private Integer v;
    }

    @Test
    public void test03() {
       UnaryOperator<List<Integer>> unaryOperator = (list) -> Optional.ofNullable(list)
                .orElse(Collections.emptyList()).stream().map(v -> v*2).collect(Collectors.toList());
        System.out.println(unaryOperator.apply(null));
        System.out.println(unaryOperator.apply(Arrays.asList(1,2,3)));
    }

    @Test
    public void test04() {
        Map<Integer, Integer> map = new HashMap<>();
//        map.putIfAbsent(1, 1);
//        map.putIfAbsent(2, 1);

        map.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue,
                e -> Lists.newArrayList(e.getKey()),
                ListUtils::union));
    }

    @Test
    public void test05() {
        List<Integer> list = Lists.newArrayList(1,2,3,4,5);
        System.out.println(list.stream().filter(v -> v%2==1).count());
    }
}
