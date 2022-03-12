package com.concurrent;

/**
 * @author benfeihu
 */
public class Test4 {
    public static void main(String[] args) {
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
