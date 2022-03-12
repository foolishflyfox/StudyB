package com.bfh.aop.impl;

import com.bfh.aop.Calculator;
import org.springframework.stereotype.Service;

/**
 * @author benfeihu
 */
@Service
public class MyMathCalculator implements Calculator {
    @Override
    public int add(int i, int j) {
        int result = i+j;
        return result;
    }

    @Override
    public int sub(int i, int j) {
        int result = i-j;
        return result;
    }

    @Override
    public int mul(int i, int j) {
        int result = i*j;
        return result;
    }

    @Override
    public int div(int i, int j) {
        int result = i/j;
        return result;
    }
}
