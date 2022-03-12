package com.bfh.bean;

import com.bfh.bean.work.Car;
import lombok.Getter;
import lombok.Setter;

/**
 * @author benfeihu
 */
public class Color {
    @Setter
    @Getter
    private Car car;
    public Color() {
        System.out.println("XXXXXX");
    }
}
