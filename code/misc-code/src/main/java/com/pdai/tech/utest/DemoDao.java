package com.pdai.tech.utest;

import java.util.Random;

/**
 * @author benfeihu
 */
public class DemoDao {

    public int getDemoStatus() {
        return new Random().nextInt();
    }
}
