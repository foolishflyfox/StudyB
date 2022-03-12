package com.bfh;

import lombok.Setter;
import lombok.ToString;

@Setter
@ToString
public class Car {
    private String name;
    private float price;
    public Car() {
        System.out.println("create Car");
    }
}
