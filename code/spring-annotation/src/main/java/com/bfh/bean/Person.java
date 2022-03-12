package com.bfh.bean;

import lombok.*;
import org.springframework.beans.factory.annotation.Value;

/**
 * @author benfeihu
 */
//@Data
@Getter
@ToString
public class Person {

    // 使用 @Value 赋值
    // 1、基本数值
    // 2、可以写 SpEL，#{}
    // 3、可以写 ${}, 取出配置文件中的值
    @Value("张三")
    private String name;
    @Value("#{20-2}")
    private Integer age;
    @Value("${person.email:defaul@email.com}")
    private String email;
//    @Value("#{miscUtils.getNickName()}")
    @Value("#{miscUtils.foo()}")
    private String nick;



    public Person(String name, Integer age) {
        this.name = name;
        this.age = age;
    }

    public Person() {
        System.out.println("Person constructor");
    }

}
