package com.bfh.json;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.List;

/**
 * @author benfeihu
 */
public class FastJsonTest {
    @Test
    public void test01() {
        List<List> lists = JSON.parseArray("[[1,1],[1,2],[2,2]]", List.class);
        List<Integer> list = lists.get(2);
        for (Integer i : list) {
            System.out.println("i = " + i);
        }
        System.out.println(lists.contains(Lists.newArrayList(1, 3)));
        List<Integer> list1 = Lists.newArrayList(1,2);
        List<Integer> list2 = Lists.newArrayList(1,2);
        System.out.println(list1.equals(list2));
    }
}
