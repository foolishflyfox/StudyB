package pdai.tech.mockito;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Random;

/**
 * @author benfeihu
 * Mockito.doAnswer 的目的是延迟计算，可以实现结果的动态返回
 */
public class DoAnswerTest {

    public int foo(int n) {
        return 0;
    }
    public static int fabonaci(int n) {
        if (n < 1) {
            return 0;
        } else if (n <= 2) {
            return 1;
        } else {
            int a = 1;
            int b = 1;
            for (int i = 3; i <= n; ++i) {
                b += a;
                a = b - a;
            }
            return b;
        }
    }
    @Test
    public void test01() {
        DoAnswerTest mock = Mockito.mock(DoAnswerTest.class);
        Mockito.doAnswer(invocation -> {
            Integer n = invocation.getArgument(0, Integer.class);
            return mock.fabonaci(n);
        }).when(mock).foo(Mockito.intThat(v -> v%2==0));
        // fabonacci  1 1 2 3 5 8 13 21
        Assert.assertEquals(21, mock.foo(8));
        Assert.assertEquals(0, mock.foo(9));
    }
}
