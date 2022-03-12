package com.bfh.math;

import org.springframework.context.annotation.ComponentScan;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

/**
 * @author benfeihu
 */
public class PrimeUtils {
    // 18014398777917439L 用了 967ms
    public boolean isPrime(long candidate) {
        if (candidate < 2) {
            return false;
        }
        return !LongStream.rangeClosed(2, (long) Math.sqrt(candidate))
                .filter(v -> candidate % v == 0).findAny().isPresent();
    }
    public List<Long> getPrimes(long bound) {
        return LongStream.rangeClosed(2, bound).filter(this::isPrime).boxed().collect(Collectors.toList());
    }

    public List<Long> getPrimes2(long bound) {
        return LongStream.rangeClosed(2, bound).boxed().collect(new Collector<Long, List<Long>, List<Long>>() {
            @Override
            public Supplier<List<Long>> supplier() {
                return ArrayList::new;
            }

            @Override
            public BiConsumer<List<Long>, Long> accumulator() {
                return (primes, v) -> {
                    Long t = (long) Math.sqrt(v);
                    boolean isPrime = true;
                    for (Long prime : primes) {
                        if (prime > t) {
                            break;
                        }
                        if (v % prime == 0) {
                            isPrime = false;
                            break;
                        }
                    }
                    if (isPrime) {
                        primes.add(v);
                    }
                };
            }

            @Override
            public BinaryOperator<List<Long>> combiner() {
                return (l1, l2) -> {
                    List<Long> list = new ArrayList<>();
                    list.addAll(l1);
                    list.addAll(l2);
                    return list;
                };
            }

            @Override
            public Function<List<Long>, List<Long>> finisher() {
                return null;
            }

            @Override
            public Set<Characteristics> characteristics() {
                Set<Characteristics> r = new HashSet<>();
                r.add(Characteristics.IDENTITY_FINISH);
                return r;
            }
        });
    }

    private boolean isPrime(List<Long> prePrimes, long candidate) {
        Long t = (long) Math.sqrt(candidate);
        for (Long prePrime : prePrimes) {
            if (prePrime > t) {
                break;
            }
            if (candidate % prePrime == 0) {
                return false;
            }
        }
        return true;
    }

    public List<Long> getPrimes3(long bound) {
        BinaryOperator<List<Long>> merge = (list1, list2) -> {
            List<Long> list = new ArrayList<>();
            list.addAll(list1);
            list.addAll(list2);
            return list;
        };
        BiConsumer<List<Long>, Long> insert = (primes, v) -> {
            if (isPrime(primes, v)) {
                primes.add(v);
            }
        };
        return LongStream.rangeClosed(2, bound).boxed().collect(Collector.of(ArrayList::new,
                insert, merge, Collector.Characteristics.IDENTITY_FINISH));
    }

    public List<Long> getPrimes4(long bound) {
        return LongStream.rangeClosed(2, bound).boxed().collect(
                ArrayList<Long>::new,
                (primes, v) -> { if(isPrime(primes, v)) {primes.add(v);} },
                ArrayList::addAll
        );
    }

}
