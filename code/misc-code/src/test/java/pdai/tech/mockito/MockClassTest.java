package pdai.tech.mockito;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author benfeihu
 */
public class MockClassTest {

    @Test
    public void mockClassTest() {
        Random mockRandom = Mockito.mock(Random.class);

        // 默认值：mock 对象的方法返回值默认都是返回类型的默认值
        System.out.println(mockRandom.nextBoolean());  // false
        System.out.println(mockRandom.nextInt());  // 0
        System.out.println(mockRandom.nextDouble());  // 0.0
    }

    @Test
    public void mockInterfaceTest() {
        List mockList = Mockito.mock(List.class);

        // 接口的默认值，和类方法一致，都是默认返回值
        Assert.assertEquals(0, mockList.size());
        Assert.assertNull(mockList.get(0));  // 不报错，但返回 null
        Assert.assertNull(mockList.get(1));

        // mock 值测试
        Mockito.when(mockList.get(0)).thenReturn("a");
        Assert.assertEquals(0, mockList.size()); // 没有指定 size() 方法返回值，这里结果仍然默认值
        Assert.assertEquals("a", mockList.get(0)); // 指定了 get(0)，返回指定的值
        Assert.assertEquals(null, mockList.get(1)); // 没指定 get(1)，返回默认值
    }
}
