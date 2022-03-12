package com.bfh.collection;

import com.bfh.reflect.ReflectUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * @author benfeihu
 */
public class InitTest {
    @Test
    public void test01() {
        // Map 初始化
        Map<Integer, String> mymap = new HashMap<Integer, String>() {{
            put(1, "one");
            put(2, "two");
            put(3, "three");
        }};
        mymap.put(4, "four");
        System.out.println(mymap);
        // Set 初始化
        {
            Set<Integer> myset1 = new HashSet<>(Arrays.asList(1, 2, 3, 4));
            System.out.println(myset1);

            Set<Integer> myset2 = new HashSet<Integer>() {{
                add(1);
                add(2);
                add(3);
            }};
            System.out.println(myset2);
        }
        // List 初始化
        {
            List<Integer> mylist1 = Arrays.asList(1, 2, 3);
            // mylist1.add(4);  // Arrays.asList 创建的列表不能被修改
            System.out.println(mylist1);

            List<Integer> mylist2 = new ArrayList<>(Arrays.asList(1, 2, 3));
            mylist2.add(4);
            System.out.println(mylist2);

            List<Integer> mylist3 = new ArrayList<Integer>() {{add(1);add(2);add(3);}};
            mylist3.add(4);
            System.out.println(mylist3);
        }
    }

    @Test
    public void test02() {
        CopyOnWriteArrayList<Integer> list = Lists.newCopyOnWriteArrayList();
//        System.out.println(Arrays.stream(list.getClass().getDeclaredMethods())
//                .map(Method::getName).sorted().collect(Collectors.joining("\n")));

        System.out.println(list);
        Object[] array1 = (Object[])ReflectUtils.invokeMethod(list, "getArray");
        System.out.println(list.hashCode());
        System.out.println(array1);

        list.add(12);
        System.out.println(list);
        Object[] array2 = (Object[])ReflectUtils.invokeMethod(list, "getArray");
        System.out.println(list.hashCode());
        System.out.println(array2);
        System.out.println(array2[0]);
        array2[0] = 22;
        System.out.println(list.get(0));
    }
}
