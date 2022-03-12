package com.readsource;

import org.junit.Assert;
import org.junit.Test;

public class StringTest {
    @Test
    public void testSubstring() {
        String s = "abcdefg";
        Assert.assertEquals("cde", s.substring(2, 5));
        Assert.assertEquals("cdefg", s.substring(2));
        Assert.assertEquals(1, s.indexOf("bc"));
        Assert.assertEquals(-1, s.indexOf("bd"));
    }

}
