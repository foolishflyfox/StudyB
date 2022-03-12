package com.concurrent;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author benfeihu
 */
public class ThreadUnsafeExample {
    private int cnt = 0;
    public void add() {
        cnt++;
    }
    public int getCnt() {
        return cnt;
    }

    public static void main(String[] args) throws InterruptedException {
        final int threadSize = 1000;
        final CountDownLatch countDownLatch = new CountDownLatch(threadSize);
        final ThreadUnsafeExample threadUnsafeExample = new ThreadUnsafeExample();
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i=0; i < threadSize; ++i) {
            executorService.execute(() -> {
                threadUnsafeExample.add();
                countDownLatch.countDown();
            });
        }
        // wait vs await
        countDownLatch.await();
        // 关闭线程池
        executorService.shutdown();
        System.out.println(threadUnsafeExample.getCnt());
    }
}
