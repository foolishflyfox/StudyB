package pdai.tech.junit;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author benfeihu
 */
public class JUnit4AssertTest {

    private int foo() {
        return 1;
    }
    public int bar() {
        return 1;
    }
    public Boolean zoo() {
        return true;
    }
    public void czz() {
        throw new RuntimeException("xxx");
    }

    @Test
    public void testAssertEquals() {
        Assert.assertEquals("断言信息，便于定位", foo(), bar());
        String s1 = new String("abc");
        String s2 = new String("abc");
        // 使用的是 Object 的 equal 方法
        // Assert.assertEquals(s1, s2);
        Assert.assertSame(s1, s2);
    }

    @Test
    public void testAssertTrueFalse() {
        Assert.assertTrue(zoo());
        Assert.assertFalse(zoo());
    }

    @Test
    public void testAssertArrayEquals() {
        String[] s1 = new String[] {new String("ab"), new String("b")};
        String[] s2 = new String[] {new String("a"), new String("b")};
        Assert.assertArrayEquals(s1, s2);
    }

    @Test(expected = RuntimeException.class)
    public void testException() {
        throw new ArithmeticException(); // 需要抛出 RuntimeException 或其子类
    }

}
