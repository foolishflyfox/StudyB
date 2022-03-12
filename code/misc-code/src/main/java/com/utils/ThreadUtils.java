package com.utils;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author benfeihu
 */
public class ThreadUtils {

    public static void simpleMultiThreadExecute(int threadCnt, Runnable runnable) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(threadCnt);
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i=0; i < threadCnt; ++i) {
            executorService.execute(() -> {
                runnable.run();
                latch.countDown();
            });
        }
        latch.await();
        executorService.shutdown();

    }
}
