package pdai.tech.base;

import org.junit.Test;

public class MiscTest {
    @Test
    public void test01() {
        // 1.1 是 double 类型，赋值给 float 精度下降，必须要强转
        float v1 = (float) 1.1;
        System.out.println(v1);
        // f 后缀指明为 float 类型，不需要强转
        float v2 = 1.1f;
        System.out.println(v2);
        // short 类型同理，默认为 int 类型
        int v3 = 0x10011;
        short v4 = (short)v3;
        System.out.println(v4);  // 17
    }
}
