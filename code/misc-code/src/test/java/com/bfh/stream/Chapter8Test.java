package com.bfh.stream;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.Test;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * @author benfeihu
 */
public class Chapter8Test {
    @Data
    @AllArgsConstructor
    class Apple {
        private int weight;
        private String color;
    }
    @Test
    public void test01() {
        List<Apple> apples = Arrays.asList(
                new Apple(10, "red"),
                new Apple(6, "yellow"),
                new Apple(11, "green")
        );
        apples.sort(Comparator.comparing(apple -> -apple.getWeight())); // 逆序
        System.out.println(apples);
        apples.sort(Comparator.comparing(Apple::getWeight)); // 顺序
        System.out.println(apples);
    }
    
}
