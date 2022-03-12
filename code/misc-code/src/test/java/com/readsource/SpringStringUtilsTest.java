package com.readsource;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

public class SpringStringUtilsTest {
    @Test
    public void testReplace() {
        Assert.assertEquals("aa/b/c.txt",
                StringUtils.replace("aa\\b\\c.txt", "\\", "/"));
    }
    @Test
    public void testDelimitedListToStringArray() {
        String s = "aa/bbb/cc";
        Assert.assertArrayEquals(new String[]{"aa", "bbb", "cc"},
                StringUtils.delimitedListToStringArray(s, "/"));
    }
    @Test
    public void testCollectionToDelimitedString() {
        List<String> s = Arrays.asList("aa", "bbb", "cc");
        Assert.assertEquals("aa:bbb:cc", StringUtils.collectionToDelimitedString(s, ":"));
    }
}
