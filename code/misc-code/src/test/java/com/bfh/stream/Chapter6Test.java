package com.bfh.stream;

import com.bfh.aop.MathPkgConfig;
import com.bfh.math.PrimeUtils;
import com.bfh.math.PrimeUtilsAspect;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

/**
 * @author benfeihu
 */
public class Chapter6Test {

    @Test
    public void test6_2() {
        List<Dish> testList = Dish.getTestList();
        System.out.println("=== 获取菜单中总共有多少种菜 ===");
        System.out.println(testList.stream().collect(Collectors.reducing(0, t -> 1, Integer::sum)));
        System.out.println(testList.stream().collect(Collectors.counting()));
        System.out.println(testList.stream().count());
        System.out.println(testList.size());

        System.out.println("\n=== 查找流中的最大值与最小值 ===");
        System.out.println(testList.stream().collect(Collectors.maxBy(Comparator.comparing(Dish::getCalories))));
        System.out.println(testList.stream().min(Comparator.comparingInt(Dish::getCalories)));
        System.out.println(testList.stream().collect(Collectors.minBy(Comparator.comparingInt(Dish::getCalories))));

        System.out.println("\n=== 求和/平均 ===");
        System.out.println(testList.stream().collect(Collectors.summingInt(Dish::getCalories)));
        System.out.println(testList.stream().collect(Collectors.averagingInt(Dish::getCalories)));
        System.out.println(testList.stream().collect(Collectors.summarizingInt(Dish::getCalories)));

        System.out.println("\n=== 连接字符串 ===");
        System.out.println(testList.stream().map(Dish::getName).collect(Collectors.joining()));
        System.out.println(testList.stream().map(Dish::getName).collect(Collectors.joining(", ")));

        System.out.println("\n=== reduce 计算总热量 ===");
        System.out.println(testList.stream().collect(Collectors.reducing(0, Dish::getCalories, (i, j) -> i+j)));
        System.out.println(testList.stream().reduce(0, (sum, v) -> sum+v.getCalories(), (s1, s2) -> s1+s2));

        System.out.println();
    }

    @Test
    public void homework_6_1() {
        List<Dish> testList = Dish.getTestList();
        System.out.println(testList.stream().map(Dish::getName).collect(Collectors.joining()));
        System.out.println(testList.stream().map(Dish::getName).collect(Collectors.reducing((s1,s2) -> s1+s2)));
        System.out.println(testList.stream().collect(Collectors.reducing("", Dish::getName, (s1, s2) -> s1+s2)));
    }

    @Test
    public void test6_3() {
        List<Dish> testList = Dish.getTestList();
        // 一级分类
        System.out.println(testList.stream()
                .collect(Collectors.groupingBy(Dish::getType, Collectors.mapping(Dish::getName, Collectors.toList()))));
        System.out.println(testList.stream()
                .collect(Collectors.groupingBy(Dish::getType,
                        Collectors.collectingAndThen(Collectors.maxBy(Comparator.comparing(Dish::getCalories)),
                                d -> d.get().getName()))));
        System.out.println(testList.stream().collect(Collectors.groupingBy(Dish::getType,
                Collectors.summingInt(Dish::getCalories))));
        System.out.println(testList.stream().collect(Collectors.groupingBy(Dish::getType,
                Collectors.mapping( d -> {
                    if (d.getCalories() <= 400) {
                        return "DIET";
                    } else if (d.getCalories() <= 700) {
                        return "NORMAL";
                    } else {
                        return "Fat";
                    }
                }, Collectors.toCollection(LinkedHashSet::new)))));
        // 二级分类
        System.out.println(testList.stream().collect(Collectors.groupingBy(Dish::getType, Collectors.groupingBy(
                dish -> {
                    if (dish.getCalories() <= 400) {
                        return "DIET";
                    } else if (dish.getCalories() <= 700) {
                        return "NORMAL";
                    } else {
                        return "FAT";
                    }
                },
                Collectors.mapping(Dish::getName, Collectors.toList())
        ))));
        // 计算二级分类数量
        System.out.println(testList.stream().collect(Collectors.groupingBy(Dish::getType, Collectors.groupingBy(
                dish -> {
                    if (dish.getCalories() <= 400) {
                        return "DIET";
                    } else if (dish.getCalories() <= 700) {
                        return "NORMAL";
                    } else {
                        return "FAT";
                    }
                },
                Collectors.counting()
        ))));
    }

    @Test
    public void testCollectorsMapping() {
        List<Dish> testList = Dish.getTestList();
        System.out.println(testList.stream().map(Dish::getName).collect(Collectors.toList()));
        System.out.println(testList.stream().collect(Collectors.mapping(Dish::getName, Collectors.toList())));
        System.out.println(testList.stream().collect(Collectors.maxBy(Comparator.comparing(Dish::getCalories))));
    }

    @Test
    public void testParation() {
        List<Dish> testList = Dish.getTestList();
        System.out.println(testList.stream().collect(Collectors.partitioningBy(Dish::isVegetarian,
                Collectors.mapping(Dish::getName, Collectors.toSet()))));
        System.out.println(testList.stream().collect(Collectors.groupingBy(Dish::isVegetarian,
                Collectors.groupingBy(Dish::getType, Collectors.mapping(Dish::getName, Collectors.toSet())))));
        System.out.println(testList.stream().collect(Collectors.partitioningBy(Dish::isVegetarian,
                Collectors.collectingAndThen(Collectors.maxBy(Comparator.comparing(Dish::getCalories)), Optional::get))));
        List<Integer> list = Arrays.asList(2,4,6);
        System.out.println(list.stream().collect(Collectors.partitioningBy(v -> v%2==0, Collectors.maxBy(Comparator.comparing(v -> v)))));
        System.out.println(list.stream().collect(Collectors.partitioningBy(v -> v%2==0)));
        System.out.println(list.stream().collect(Collectors.groupingBy(v -> v%2)));
        System.out.println(list.stream().collect(Collectors.mapping(v -> v*2, Collectors.toCollection(HashSet::new))).toString());
    }

    @Test
    public void test6_5() {
        List<Integer> list = Arrays.asList(2,3,4);
        System.out.println(list.stream().map(v -> v*2).collect(new ToListCollector<Integer>()));
    }

    @Test
    public void test6_6() {
        long v = 18014398777917439L;
//        long v = 1073807359;
        long t0 = System.currentTimeMillis();
        PrimeUtils primeUtils = new PrimeUtils();
        boolean isPrime = primeUtils.isPrime(v);
        long t1 = System.currentTimeMillis();
        System.out.println(String.format("use %s ms, %d is %s primme", t1-t0, v, isPrime?"a":"not"));
    }

    @Test
    public void testPrime() {
        ApplicationContext context = new AnnotationConfigApplicationContext(MathPkgConfig.class);
        PrimeUtils primeUtils = context.getBean(PrimeUtils.class);
        System.out.println(primeUtils.isPrime(18014398777917439L));
        long bound = 3000000;
        System.out.println(primeUtils.getPrimes(bound).size());
        System.out.println(primeUtils.getPrimes2(bound).size());
        System.out.println(primeUtils.getPrimes3(bound).size());
        System.out.println(primeUtils.getPrimes4(bound).size());
    }

}
