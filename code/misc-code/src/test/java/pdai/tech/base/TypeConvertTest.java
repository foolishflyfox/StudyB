package pdai.tech.base;

import com.utils.StringFormatUtils;
import org.junit.Test;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TypeConvertTest {
    @Test
    public void test01() {
        byte[] data = new byte[] {108, 101, 5, 65, -1};
        System.out.println(Arrays.toString(data));
        System.out.println(new String(data));
        // byte 转 16 进制字符串显示
        System.out.println(StringFormatUtils.hexString(data, ","));
    }

    @Test
    public void test02() {
        byte v = 100;
        long a = (long)v;
        System.out.println(a);
    }

    @Test
    public void test03() {
        byte a = 127;
        byte b = 127;
        // b = a + b; // 如 byte、short 或者 int，首先会将它们提升到 int 类型，然后在执行加法操作。
        b += a;
        System.out.println(b);
    }
}
