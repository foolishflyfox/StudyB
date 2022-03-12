package com.concurrent;

import lombok.SneakyThrows;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author benfeihu
 */
public class Test3 {
    static final Object obj = new Object();
    static class MyThread extends Thread {
        private volatile boolean stopped = false;
        public void run() {
            while(!stopped) {
                try {
                    synchronized (obj) {  // synchronized BLOCKED 重量级阻塞
                        System.out.println("t.isInterrupted() = " +isInterrupted());
                        System.out.println("t.isInterrupted() = " +isInterrupted());
                        System.out.println("Thread.interrupted() = " + Thread.interrupted());
                        System.out.println("Thread.interrupted() = " + Thread.interrupted());
                        System.out.println("aaa");
                        obj.wait();  // WAITING wait 是轻量级阻塞
                        System.out.println("bbb");
                    }
                    synchronized (obj) {
                        int a = 1, b = 2;
                        int c = a + b;
                        System.out.println("yyy");
                    }
                } catch (Exception e) {
                    System.out.println("t.isInterrupted() = " +isInterrupted());
                    System.out.println("zz");
                }
            }
        }
        public void setStop() {
        }
    }

    @SneakyThrows
    public static void main(String[] args) {
        MyThread t = new MyThread();
        t.start();
//        Thread.sleep(100);
        synchronized (obj) {
            Thread.sleep(100);
            System.out.println(t.getState());
            t.setStop();
            t.interrupt();
        }
        System.out.println("AAA");
        t.join();
    }
}
