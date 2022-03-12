package pdai.tech.mockito;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * @author benfeihu
 */
public class DoThrowTest {

    static class ExampleService {
        public void hello() {
            System.out.println("Hello");
        }
        public int foo() {
            return 123;
        }
    }

    private ExampleService exampleService = Mockito.mock(ExampleService.class);

    /**
     * 对于返回类型是 void 的函数，thenThrow 是无效的，要使用 doThrow
     */
    @Test
    public void test1() {
        Mockito.doThrow(new RuntimeException("异常1")).when(exampleService).hello();
        try {
            exampleService.hello();
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(e instanceof RuntimeException);
            Assert.assertEquals("异常1", e.getMessage());
        }
    }

    /**
     * doThrow 同样适用于有返回值的函数调用
     */
    @Test
    public void test2() {
        Mockito.doThrow(new RuntimeException("异常2")).when(exampleService).foo();
        try {
            exampleService.foo();
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(e instanceof RuntimeException);
            Assert.assertEquals("异常2", e.getMessage());
        }
    }
}
