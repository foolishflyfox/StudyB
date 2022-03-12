package pdai.tech.mockito;

import org.junit.Test;
import org.mockito.Mockito;

/**
 * @author benfeihu
 * Mockito 默认是不支持静态方法的
 */
public class StaticMethodTest {
    static class ExampleService {
        public static int add(int a, int b) {
            return a + b;
        }
    }

    @Test
    public void test() {
        Mockito.when(ExampleService.add(1, 2)).thenReturn(100);  // 报错，Mockito 默认不支持对静态方法打桩
    }
}
