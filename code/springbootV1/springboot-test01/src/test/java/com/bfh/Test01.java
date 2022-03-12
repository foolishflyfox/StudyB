package com.bfh;

import com.bfh.bean.A;
import com.bfh.bean.Person;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

@SpringBootTest
@RunWith(SpringRunner.class)
public class Test01 {
    @Autowired
    Person person;
    // 可以以 map 的形式获取容器中所有相同类型的组件
    @Autowired
    Map<String, A> objs;
    @Autowired
    Map<String, Person> people;

    @Test
    public void contextLoader() {
        System.out.println(person);
        System.out.println(objs);
        System.out.println(people);
        System.out.println(person == people.get("person"));
    }
}
