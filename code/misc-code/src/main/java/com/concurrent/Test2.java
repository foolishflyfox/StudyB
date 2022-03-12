package com.concurrent;

import lombok.SneakyThrows;

/**
 * @author benfeihu
 */
public class Test2 {
    static class MyThread extends Thread {
        private volatile boolean stopped = false;
        public void run() {
            while(!stopped) {
                try {
                    System.out.println("t2 is executing");
                    Thread.sleep(500);
                } catch (InterruptedException e) {

                }
            }
        }
        public void setStop() {
            this.stopped = true;
        }
    }

    @SneakyThrows
    public static void main(String[] args) {
        MyThread t = new MyThread();
        t.start();
        t.setStop();
        t.join();
    }
}
