package com.bfh.collection;

import lombok.SneakyThrows;
import org.junit.Test;

import java.util.concurrent.SynchronousQueue;

/**
 * @author benfeihu
 */
public class SynchronousQueueTest {
    @SneakyThrows
    @Test
    public void test01() {
        SynchronousQueue<Integer> synchronousQueue = new SynchronousQueue<>(true);
        Runnable r = () -> {
            try {
                System.out.println(Thread.currentThread().getName() + " enter");
                Integer v = synchronousQueue.take();
                System.out.println(System.currentTimeMillis() + " : " +
                        Thread.currentThread().getName() + " get " + v);
            } catch (Exception e) {
                System.out.println(e);
            }
        };
        new Thread(r).start();
        Thread.sleep(10);
        new Thread(r).start();
        Thread.sleep(10);
        new Thread(r).start();
        System.out.println(System.currentTimeMillis());
        Thread.sleep(1000);
        synchronousQueue.put(1);
        Thread.sleep(500);
        synchronousQueue.put(2);
    }

    @Test
    public void test02() {
        // 不会无限循环
        new Thread(() -> {
            for (;;) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
            }
        }).start();
    }
}
