package com.bfh.iterator;

import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.Iterator;
import java.util.List;

/**
 * @author benfeihu
 */
public class IteratorTest {
    @Test
    public void test01() {
        List<Integer> list1 = Lists.newArrayList(1,2,3,3,5,6,7);
        for (Iterator<Integer> each = list1.iterator(); each.hasNext(); ) {
            if (each.next() % 3==0) {
                each.remove();
            }
        }
        System.out.println(list1);

        List<Integer> list2 = Lists.newArrayList(1,2,3,3,5,6,7);
        for (int i = 0; i < list2.size(); ++i) {
            if (list2.get(i) % 3==0) {
                list2.remove(i);
            }
        }
        // 结果错误：[1, 2, 3, 5, 7]
        System.out.println(list2);

        List<Integer> list3 = Lists.newArrayList(1,2,3,3,5,6,7);
        list3.removeIf(v -> v%3==0);
        System.out.println(list3);

    }
}
