package pdai.tech.base;

import org.junit.Test;

public class SwitchTest {
    @Test
    public void test01() {
        String s = "a";
        // switch 中可以为 String 类型
        switch (s) {
            case "1":
                System.out.println("111");
                break;
            case "a":
                System.out.println("aaa");
                break;
            default:
                System.out.println("other");
                break;
        }
    }

    @Test
    public void test02() {
        long v = 23;
//        switch (v) {  // 编译出错，不能为 long 类型
//            case 0:
//                System.out.println("v = 0");
//                break;
//        }
    }
}
