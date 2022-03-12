package pdai.tech.mockito;

import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Random;

/**
 * @author benfeihu
 */
public class DoNothingTest {

    public void foo() {}

    /**
     * doNothing 可以控制 doThrow 的时机
     */
    @Test
    public void test1() {
        DoNothingTest mock = Mockito.mock(DoNothingTest.class);
        Mockito.doNothing().doThrow(new RuntimeException("异常")).when(mock).foo();
        mock.foo();
        try {
            mock.foo();
        } catch (Exception e) {
            Assert.assertTrue(e instanceof RuntimeException);
            Assert.assertEquals("异常", e.getMessage());
        }
    }
}
