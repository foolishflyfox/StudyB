package com.bfh.stream;

import com.bfh.log.TimeUsageLog;
import lombok.SneakyThrows;

import java.util.List;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;

/**
 * @author benfeihu
 */
public class PerformanceDemo {

    @TimeUsageLog
    public long sumIterate(long n) {
        long sum = 0;
        for (long i=1; i <= n; ++i) {
            sum += i;
        }
        return sum;
    }

    @TimeUsageLog
    public long sumStreamSequence(long n) {
        return LongStream.rangeClosed(1, n).reduce(0, Long::sum);
    }

    @TimeUsageLog
    public long sumStreamParallel(long n) {
        return LongStream.rangeClosed(1, n).parallel().reduce(0, Long::sum);
    }

    @TimeUsageLog
    public long sumStreamParallel0(long n) {
        return Stream.iterate(1L, v -> v+1).limit(n).parallel().reduce(0L, Long::sum);
    }

    @TimeUsageLog
    public long sumStreamSequence0(long n) {
        return Stream.iterate(1L, v -> v+1).limit(n).reduce(0L, Long::sum);
    }

    @TimeUsageLog
    public long sumStreamParallel0B(long n) {
        return Stream.generate(new Supplier<Long>() {
            private Long v = 1L;
            @Override
            public synchronized Long get() {
                return v++;
            }
        }).limit(n).parallel().reduce(0L, Long::sum);
    }

    @TimeUsageLog
    public long sumStreamSequence0B(long n) {
        return Stream.generate(new Supplier<Long>() {
            private Long v = 1L;
            @Override
            public synchronized Long get() {
                return v++;
            }
        }).limit(n).reduce(0L, Long::sum);
    }

    @TimeUsageLog
    public long sumStreamParallel0C(long n) {
        return LongStream.rangeClosed(1, n).boxed().parallel().reduce(0L, Long::sum);
    }

    @TimeUsageLog
    public long sumStreamSequence0C(long n) {
        return LongStream.rangeClosed(1, n).boxed().reduce(0L, Long::sum);
    }

    Random random = new Random();
    @SneakyThrows
    private long getRandom() {
//        System.out.println(Thread.currentThread().getName());
        Thread.sleep(50);
        return random.nextLong() % 10000;
    }

    @TimeUsageLog
    public List<Long> orderLimit(long n) {
        return Stream.generate(this::getRandom).limit(n).map(v -> v*v).collect(Collectors.toList());
    }
    @TimeUsageLog
    public List<Long> orderParallelLimit(long n) {
        return Stream.generate(this::getRandom)
                .peek(v -> System.out.println(Thread.currentThread().getName() + " " + System.currentTimeMillis() + ", v = " + v))
                .parallel().limit(n)
                .map(v -> {
//                    System.out.println(System.currentTimeMillis() + ":" + Thread.currentThread().getName() + " " + v);
                    return v*v;
                }).collect(Collectors.toList());
    }
    @TimeUsageLog
    public List<Long> unorderLimit(long n) {
        return Stream.generate(this::getRandom).unordered().limit(n).map(v -> v*v).collect(Collectors.toList());
    }
    @TimeUsageLog
    public List<Long> unorderParallelLimit(long n) {
        return Stream.generate(this::getRandom).unordered().parallel().limit(n).map(v -> v*v).collect(Collectors.toList());
    }



}
