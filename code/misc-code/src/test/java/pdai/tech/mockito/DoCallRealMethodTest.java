package pdai.tech.mockito;

import org.junit.Test;
import org.mockito.Mockito;

/**
 * @author benfeihu
 */
public class DoCallRealMethodTest {
    void foo() {
        System.out.println("foo this = " + this);
    }
    @Test
    public void test1() {
        DoCallRealMethodTest mock = Mockito.mock(DoCallRealMethodTest.class);
        mock.foo();  // 啥都不干，默认 doNothing
    }

    @Test
    public void test2() {
        DoCallRealMethodTest mock = Mockito.mock(DoCallRealMethodTest.class);
        Mockito.doCallRealMethod().when(mock).foo();
        mock.foo();  // 打印 "foo this = Mock for DoCallRealMethodTest, hashCode: 2049935277"
    }
}
