package com.bfh.aop.impl;

import org.springframework.stereotype.Service;

/**
 * @author benfeihu
 */
@Service
public class MyMathCalculatorV2 {

    public int add(int i, int j) {
        int result = i+j;
        return result;
    }

    public double add(int i, double j) {
        return (i+j);
    }

    public int sub(int i, int j) {
        int result = i-j;
        return result;
    }

    public int mul(int i, int j) {
        int result = i*j;
        return result;
    }

    public int div(int i, int j) {
        int result = i/j;
        return result;
    }

}
