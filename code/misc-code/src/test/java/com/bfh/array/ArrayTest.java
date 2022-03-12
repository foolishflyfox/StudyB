package com.bfh.array;

import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.List;

/**
 * @author benfeihu
 */
public class ArrayTest {
    @Test
    public void test01() {
        String[] ss = new String[] {"a", "bb"};
        foo(1, ss);
    }

    @Test
    public void test02() {
        List<Integer> list = Lists.newArrayList(1,2,3,4);
//        Integer[] a = list.toArray();
    }

    public void foo(Integer v, String... ss) {
        for (String s : ss) {
            System.out.println("s = " + s);
        }
    }
}
