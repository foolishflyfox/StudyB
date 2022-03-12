package pdai.tech.mockito;

import org.junit.Test;
import org.mockito.Mockito;

/**
 * @author benfeihu
 */
public class TestIsolate {

    static class ExampleService {
        public int add(int a, int b) {
            return a + b;
        }
    }

    private ExampleService exampleService = Mockito.mock(ExampleService.class);

    @Test
    public void test01() {
        System.out.println("--- call test01 ---");
        System.out.println("打桩前: " + exampleService.add(1, 2));
        Mockito.when(exampleService.add(1, 2)).thenReturn(100);
        System.out.println("打桩后: " + exampleService.add(1, 2));
    }
    @Test
    public void test02() {
        System.out.println("--- call test01 ---");
        System.out.println("打桩前: " + exampleService.add(1, 2));
        Mockito.when(exampleService.add(1, 2)).thenReturn(200);
        System.out.println("打桩后: " + exampleService.add(1, 2));
    }
}
