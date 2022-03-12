package pdai.tech.mockito;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Random;

/**
 * @author benfeihu
 */
@RunWith(MockitoJUnitRunner.class)
public class MockAnnotationTest {
    @Mock
    Random random;

    /**
     * 例子：多次调用，每次返回不同结果
     */
    @Test
    public void test01() {
        Mockito.when(random.nextInt()).thenReturn(1).thenReturn(2).thenReturn(3).thenReturn(4).thenReturn(5);
        Assert.assertEquals(random.nextInt(), 1);
        Assert.assertEquals(random.nextInt(), 2);
        Assert.assertEquals(random.nextInt(), 3);
        Assert.assertEquals(random.nextInt(), 4);
        Assert.assertEquals(random.nextInt(), 5);
    }

    /**
     * 与 test01 等价
     */
    @Test
    public void test02() {
        Mockito.when(random.nextInt()).thenReturn(1,2,3,4,5);
        Assert.assertEquals(random.nextInt(), 1);
        Assert.assertEquals(random.nextInt(), 2);
        Assert.assertEquals(random.nextInt(), 3);
        Assert.assertEquals(random.nextInt(), 4);
        Assert.assertEquals(random.nextInt(), 5);
    }



}
