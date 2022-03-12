package com.bfh.stream;

import org.junit.Test;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

public class ParallelStreamTest {

    static Long n = 5000000L;

    private void foo(Function<List<Long>, Long> func) {
        List<Long> list = LongStream.rangeClosed(0, n).boxed().collect(Collectors.toList());
        long t0 = System.currentTimeMillis();
        Long r = func.apply(list);
        long t1 = System.currentTimeMillis();
        System.out.println((t1-t0) + "ms : " + r);
    }
    private Long getResult() {
        return (1+n)*n;
    }
    @Test
    public void test01() {
        System.out.println(getResult());
        foo((list) -> list.stream().map(v -> v*2).reduce(Long::sum).orElse(0L));
    }
    @Test
    public void test02() {
        foo((list) -> list.parallelStream().map(v -> v*2).reduce(Long::sum).orElse(0L));
    }
    @Test
    public void test03() {
        ForkJoinPool fjp = new ForkJoinPool(1);
        foo((list) -> {
            try {
                return fjp.submit(() -> list.parallelStream().map(v -> v*2)
                        .reduce(Long::sum).orElse(0L)).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            return 0L;
        });
    }

    private int foo(int t) {
        if (t % 5 == 0) {
            throw new RuntimeException();
        }
        return t;
    }

    @Test
    public void testForkJoinParallelStream() {
        try {
            List<Integer> list = new ForkJoinPool(20).submit(() -> IntStream.rangeClosed(1, 20)
                    .parallel().map(v -> {
                        int r = 0;
                        try {
                            r = foo(v);
                        } catch (Exception e) {
                            return -1;
                        }
                        return r;
                    }).boxed().collect(Collectors.toList())).get();
            System.out.println(list);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}
