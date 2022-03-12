package com.bfh.concurrent;

import com.google.common.collect.Lists;
import com.utils.DateTimeUtils;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

/**
 * @author benfeihu
 */
public class ForkJoinTest {

    @Test
    public void test01() throws ExecutionException, InterruptedException {
        ForkJoinPool forkJoinPool = new ForkJoinPool(3);
        List<Integer> list = Lists.newArrayList(2, 4, 1, 6, 7);
        List<Integer> values = forkJoinPool.submit(() -> list.parallelStream().map(v -> v*1000).map(v -> {
            System.out.println(Thread.currentThread().getName());
            DateTimeUtils.sleep(v);
            return v;
        }).collect(Collectors.toList())).get();
        System.out.println(values);
    }
}
