package com.bfh.optional.v2;

import lombok.Getter;

import java.util.Optional;

/**
 * @author benfeihu
 */
public class Person {

    private Optional<Car> car;

    @Getter
    int age;

    public Optional<Car> getCar() {
        return car;
    }
}
