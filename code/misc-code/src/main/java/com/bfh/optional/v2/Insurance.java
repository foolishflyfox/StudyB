package com.bfh.optional.v2;

/**
 * @author benfeihu
 */
public class Insurance {

    private String name;

    public String getName() {
        // 不用 null == name 的判断，因为出现 NPE 是有问题的，就应该抛出异常
        return name;
    }
}
