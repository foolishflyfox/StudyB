package com.bfh.concurrent;

import lombok.SneakyThrows;
import org.junit.Test;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author benfeihu
 */
public class ListsTest {

    @Test
    public void test01() {
//         putTest(new LinkedBlockingQueue<>());
        putTest(new ConcurrentLinkedQueue<>());
    }

    @SneakyThrows
    private void putTest(Queue<Integer> queue) {
        // System.out.println("Main " + System.currentTimeMillis() + " start");
        Long t0 = System.currentTimeMillis();
        Runnable r = () -> {
            Long subT0 = System.currentTimeMillis();
            // System.out.println(Thread.currentThread().getName() + " " + System.currentTimeMillis() + " start");
            for (int i=0; i<100000; ++i) {
                queue.add(i);
            }
            System.out.println(Thread.currentThread().getName() + " " + (System.currentTimeMillis()-subT0));
        };
        int n = 100;
        Thread[] threads = new Thread[n];
        for (int i = 0; i < n; ++i) {
            threads[i] = new Thread(r);
            threads[i].start();
        }
        for (int i = 0; i < n; ++i) {
            threads[i].join();
        }
        System.out.println("Main " + (System.currentTimeMillis()-t0) + "    " + queue.size());
    }
}
