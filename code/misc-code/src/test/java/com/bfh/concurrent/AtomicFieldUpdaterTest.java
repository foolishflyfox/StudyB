package com.bfh.concurrent;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.LongAdder;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author benfeihu
 */
public class AtomicFieldUpdaterTest {

    @AllArgsConstructor
    @Data
    static class A {
        protected volatile int avalue;
    }

    @Test
    public void test01() {
        AtomicIntegerFieldUpdater updater = AtomicIntegerFieldUpdater.newUpdater(A.class, "avalue");
        A a = new A(12);
        int prev = updater.getAndIncrement(a);
        System.out.println(prev);
        System.out.println(a.getAvalue());
    }

    @Test
    public void test02() {
        foo();
    }

    synchronized void foo() {
        System.out.println("foo");
        bar();
    }
    synchronized void bar() {
        System.out.println("bar");
    }
}
