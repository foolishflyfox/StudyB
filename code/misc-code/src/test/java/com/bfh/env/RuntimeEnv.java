package com.bfh.env;

import org.junit.Test;

/**
 * @author benfeihu
 */
public class RuntimeEnv {
    @Test
    public void test01() {
        // 获取机器核数
        System.out.println("机器可用cpu核数 = " + Runtime.getRuntime().availableProcessors());
    }
}
