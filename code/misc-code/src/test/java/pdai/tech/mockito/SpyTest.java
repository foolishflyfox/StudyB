package pdai.tech.mockito;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.nio.file.attribute.UserPrincipal;

/**
 * @author benfeihu
 */
public class SpyTest {
    static class ExampleService {
        int add(int a, int b) {
            return a + b;
        }
    }

    @Spy
    private ExampleService mExampleService;

    @Test
    public void testSpy() {
        ExampleService spyExampleService = Mockito.spy(new ExampleService());

        // 默认会走真实方法
        Assert.assertEquals(3, spyExampleService.add(1,2));

        // 打桩后，不会走了
        Mockito.when(spyExampleService.add(1, 2)).thenReturn(10);
        Assert.assertEquals(10, spyExampleService.add(1, 2));

        // 但是参数不匹配的调用，仍然走真实方法
        Assert.assertEquals(3, spyExampleService.add(2, 1));
    }

    @Test
    public void testMock() {
        ExampleService exampleService = Mockito.mock(ExampleService.class);
        // mock 的对象默认不会走真实调用
        Assert.assertEquals(0, exampleService.add(1,2));
    }

    @Test
    public void testSpyAnnotation() {
        MockitoAnnotations.openMocks(this);

        Assert.assertEquals(3, mExampleService.add(1,2));

        Mockito.when(mExampleService.add(1,2)).thenReturn(10);
        Assert.assertEquals(10, mExampleService.add(1,2));
        Assert.assertEquals(3, mExampleService.add(2,1));
    }
}
