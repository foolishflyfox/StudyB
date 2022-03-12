package com.bfh.aop;

import org.springframework.stereotype.Component;

@Component
public class MathCalculator {
    public int div(int i, int j) {
        return i/j;
    }
    public int add(int i, int j, int k) {
        return i+j+k;
    }
    public int add(int i, int j) {
        return i+j;
    }
}
