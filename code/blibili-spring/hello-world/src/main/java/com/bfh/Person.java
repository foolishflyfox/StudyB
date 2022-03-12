package com.bfh;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Map;
import java.util.Properties;

@Getter
@Setter
@ToString
public class Person {
    private String lastName;
    private Integer age;
    private String gender;
    private String email;
    private Car car;
    private List<Book> books;
    private Map<String, Object> maps;
    private Properties properties;
    private float salary;

    public Person(Car car1) {
        this.car = car1;
    }

    public Person(String lastName, Integer age, String gender, String email) {
        this.lastName = lastName;
        this.age = age;
        this.gender = gender;
        this.email = email;
        System.out.println("argu constructor");
    }

    public Person() {
        System.out.println("create Person");
    }
}
