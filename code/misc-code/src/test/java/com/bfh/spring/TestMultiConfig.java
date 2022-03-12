package com.bfh.spring;

import com.bfh.config.spring.CommonConfig;
import com.bfh.config.spring.TempConfig;
import com.bfh.log.TimeUsageLogAspect;
import com.bfh.stream.ForkJoinSumCalculator;
import com.bfh.stream.PerformanceDemo;
import lombok.Getter;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.LongConsumer;
import java.util.stream.LongStream;

/**
 * @author benfeihu
 */
public class TestMultiConfig {
    @Test
    public void test01() {
        final long n = 3000000000L;
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(CommonConfig.class, TempConfig.class);
        PerformanceDemo performanceDemo = context.getBean(PerformanceDemo.class);
        // 742ms
        System.out.println(performanceDemo.sumIterate(n));
        // 178ms
        System.out.println(performanceDemo.sumStreamParallel(n));
        // 3904ms
        System.out.println(performanceDemo.sumStreamSequence(n));
        // 太慢了。。。
        // System.out.println(performanceDemo.sumStreamParallel0(n));

    }

    // 容器动态注册bean
    @Test
    public void test02() {
        final long n = 3000000000L;
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(CommonConfig.class);
        // 动态注册 bean
        context.register(PerformanceDemo.class);
        PerformanceDemo performanceDemo = context.getBean(PerformanceDemo.class);
//        PerformanceDemo performanceDemo = new PerformanceDemo();
        // 742ms
        System.out.println(performanceDemo.sumIterate(n));
        // 178ms
        System.out.println(performanceDemo.sumStreamParallel(n));
        // 3904ms
        System.out.println(performanceDemo.sumStreamSequence(n));
    }

    @Test
    public void test03() {
        final long n = 30000000L;
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(CommonConfig.class, TempConfig.class);
        PerformanceDemo performanceDemo = context.getBean(PerformanceDemo.class);

        // 并行耗时比串行耗时更多，因为通过 iterate 生成的流有前后依赖关系，并行过程中需要等待，反而增加了耗时
        // 3022 ms
        System.out.println(performanceDemo.sumStreamParallel0(n));
        // 506 ms
        System.out.println(performanceDemo.sumStreamSequence0(n));

    }

    @Test
    public void test04() {
        final long n = 30000000L;
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(CommonConfig.class, TempConfig.class);
        PerformanceDemo performanceDemo = context.getBean(PerformanceDemo.class);

        // 3351 ms
        System.out.println(performanceDemo.sumStreamParallel0B(n));
        // 637 ms
        System.out.println(performanceDemo.sumStreamSequence0B(n));

    }

    @Test
    public void test05() {
        final long n = 600000000L;
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(CommonConfig.class, TempConfig.class);
        PerformanceDemo performanceDemo = context.getBean(PerformanceDemo.class);

        // 2782 ms
        System.out.println(performanceDemo.sumStreamParallel0C(n));
        // 3997 ms
        System.out.println(performanceDemo.sumStreamSequence0C(n));

    }

    @Test
    public void test06() {
        class Accumulater {
            @Getter
            private long v = 0;
            public void add(long t) {
                v += t;
            }
        };
        class Accumulater2 {
            private AtomicLong v = new AtomicLong(0);
            public void add(long t) {
                v.addAndGet(t);
            }
            public long getV() {
                return v.get();
            }
        };
        Accumulater a = new Accumulater();
        LongStream.rangeClosed(1, 3000).parallel().forEach(a::add);
        System.out.println(a.getV());

        Accumulater2 a2 = new Accumulater2();
        LongStream.rangeClosed(1, 3000).parallel().forEach(a2::add);
        System.out.println(a2.getV());
    }

    @Test
    public void test07() {
        int n = 2;
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(CommonConfig.class, TempConfig.class);
        PerformanceDemo performanceDemo = context.getBean(PerformanceDemo.class);
//        System.out.println(performanceDemo.orderLimit(n).size());
        System.out.println(performanceDemo.orderParallelLimit(n).size());
//        System.out.println(performanceDemo.unorderLimit(n).size());
//        System.out.println(performanceDemo.unorderParallelLimit(n).size());
    }

    @Test
    public void test08() {
        long[] numbers = LongStream.rangeClosed(1, 100).toArray();
        ForkJoinSumCalculator forkJoinSumCalculator = new ForkJoinSumCalculator(numbers);
        System.out.println(new ForkJoinPool().invoke(forkJoinSumCalculator));
    }
}
