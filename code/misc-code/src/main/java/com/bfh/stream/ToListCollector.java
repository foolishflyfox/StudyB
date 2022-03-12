package com.bfh.stream;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * @author benfeihu
 */
public class ToListCollector <T> implements Collector<T, List<T>, List<T>> {
    // 建立新的结果容器
    @Override
    public Supplier<List<T>> supplier() {
        return ArrayList::new;
    }

    // 将元素添加到结果容器中
    @Override
    public BiConsumer<List<T>, T> accumulator() {
        return List::add;
    }

    // 合并两个结果容器
    @Override
    public BinaryOperator<List<T>> combiner() {
        return (list1, list2) -> {
            List<T> newList = new ArrayList<>();
            newList.addAll(list1);
            newList.addAll(list2);
            return newList;
        };
    }

    // 对结果容器应用最终转换：finisher 方法
    @Override
    public Function<List<T>, List<T>> finisher() {
        System.out.println("finisher called");
        return Function.identity();
    }


    // 定义了收集器的行为，尤其是关于六是否可以进行并行归约，以及可以使用哪些优化的提示
    // UNORDERed 归约结果不受流中项目的遍历和累计顺序影响
    // CONCURRENT accumulator 函数可以从多个线程同时调用，且该收集器可以并行归约流。如果收集器没有表 UNORDERED，那它仅在用于无序数据源时才可归约
    // IDENTITY_FINISH 完成器方法返回的结果是一个恒等结果，可以跳过
    @Override
    public Set<Characteristics> characteristics() {
//        return new HashSet<>(Collections.singletonList(Characteristics.IDENTITY_FINISH));
        return new HashSet<>(Arrays.asList(Characteristics.IDENTITY_FINISH, Characteristics.CONCURRENT));
    }
}
