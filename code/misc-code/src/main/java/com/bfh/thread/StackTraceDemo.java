package com.bfh.thread;

/**
 * @author benfeihu
 */
public class StackTraceDemo {
    public void foo() {
        System.out.println("start of foo");
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        System.out.println(stackTrace.length);
        for (StackTraceElement stackTraceElement : stackTrace) {
            System.out.println( stackTraceElement.getFileName() + ":" +
                    stackTraceElement.getMethodName() + ":" + stackTraceElement.getLineNumber());
        }
        System.out.println(
                getLogPrefix());
    }

    public static void main(String[] args) {
        StackTraceDemo std = new StackTraceDemo();
        std.foo();
    }

    public static String getLogPrefix() {
        Thread thread = Thread.currentThread();
        StackTraceElement ste = thread.getStackTrace()[2];
        return " jasper-log " + ste.getFileName() + ":" + ste.getLineNumber()
                + ":" + ste.getClassName() + ":" + ste.getMethodName() + " ";
    }
}
