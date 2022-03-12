package com.bfh.collection;

import org.junit.Test;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

/**
 * @author benfeihu
 */
public class CollectionTest {
    @Test
    public void test01() {
        Deque<Integer> deque = new ArrayDeque<>(Arrays.asList(1,2,3));
        Integer[] integers = deque.toArray(new Integer[0]);
        System.out.println(Arrays.toString(integers));
        System.out.println(deque);
    }
}
