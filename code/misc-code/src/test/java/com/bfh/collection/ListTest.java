package com.bfh.collection;

import com.google.common.collect.Lists;
import org.apache.commons.collections.ListUtils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author benfeihu
 */
public class ListTest {
    @Test
    public void test01() {
        List<Integer> l1 = Lists.newArrayList(1,2,3);
        List<Integer> l2 = Lists.newArrayList(4,5);
        System.out.println(ListUtils.union(Arrays.asList(0), l2));
    }

    @Test
    public void test02() {
        System.out.println(IntStream.rangeClosed(0, 100).boxed().collect(Collectors.toList()));
        Lists.partition(IntStream.rangeClosed(0, 100).boxed().collect(Collectors.toList()), 30).forEach(list -> {
            System.out.println(list);
        });
    }

    @Test
    public void test03(){
//        String[] array = new String[2];
        Integer[] array = new Integer[] {1,2,3};
        List list = Arrays.asList(array);
        //对转换后的list插入一条数据
//        list.add("1");
        System.out.println(list);
    }

}
