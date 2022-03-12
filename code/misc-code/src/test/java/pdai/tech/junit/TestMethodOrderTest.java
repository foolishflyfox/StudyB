package pdai.tech.junit;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

//@FixMethodOrder(MethodSorters.NAME_ASCENDING)  // 按测试函数的名字进行测试
@FixMethodOrder(MethodSorters.DEFAULT)
//@FixMethodOrder(MethodSorters.J®VM)
public class TestMethodOrderTest {
    @Test
    public void testB() {
        System.out.println("in testB");
    }
    @Test
    public void testA() {
        System.out.println("in testA");
    }
    @Test
    public void testC() {
        System.out.println("in testC");
    }
}
