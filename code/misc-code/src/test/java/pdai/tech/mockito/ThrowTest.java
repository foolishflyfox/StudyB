package pdai.tech.mockito;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Random;

/**
 * @author benfeihu
 */
public class ThrowTest {
    /**
     * 例1: thenThrow 用来让函数调用抛出异常
     */
    @Test
    public void throwTest1() {
        Random random = Mockito.mock(Random.class);
        Mockito.when(random.nextInt()).thenThrow(new RuntimeException("异常"));

        try {
            random.nextInt();
            Assert.fail();
        } catch (Exception ex ) {
            Assert.assertTrue(ex instanceof RuntimeException);
            Assert.assertEquals("异常", ex.getMessage());
        }
    }

    /**
     * thenThrow 中可以指定多个异常，在调用时异常依次出现。若调用次数超过异常的数量，再次调用时抛出最后一个异常
     */
    @Test
    public void throwTest2() {
        Random random = Mockito.mock(Random.class);
        Mockito.when(random.nextInt()).thenThrow(new RuntimeException("异常1"), new RuntimeException("异常2"));
        try {
            random.nextInt();
            Assert.fail();
        } catch (Exception ex) {
            Assert.assertTrue(ex instanceof RuntimeException);
            Assert.assertEquals("异常1", ex.getMessage());
        }

        try {
            random.nextInt();
            Assert.fail();
        } catch (Exception ex) {
            Assert.assertTrue(ex instanceof RuntimeException);
            Assert.assertEquals("异常2", ex.getMessage());
        }

        try {
            random.nextInt();
            Assert.fail();
        } catch (Exception ex) {
            Assert.assertTrue(ex instanceof RuntimeException);
            Assert.assertEquals("异常2", ex.getMessage());
        }
    }

}
