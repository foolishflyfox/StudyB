package pdai.tech.math;

import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * 如果我们需要精确计算的结果，必须使用 BigDecimal 类来操作，但性能比 float、double 要差
 * 尽量使用参数类型为 String 的构造函数
 * BigDecimal 都是不可变的，在进行每一次四则运算时，都会产生一个新的对象，所以在做加减乘除运算时要记得保存操作后的值
 */
public class BigDecimalTest {
    @Test
    public void test01() {
        BigDecimal a = new BigDecimal(0.1);
        System.out.println("a = " + a);  // a = 0.1000000000000000055511151231257827021181583404541015625
        BigDecimal b = new BigDecimal("0.1");
        System.out.println("b = " + b);  // b = 0.1
        BigDecimal c = BigDecimal.valueOf(0.1);
        System.out.println("c = " + c);  // c = 0.1
        BigDecimal d = new BigDecimal(String.valueOf(0.1));
        System.out.println("d = " + d);  // d = 0.1
        System.out.println("a.doubleValue() = " + a.doubleValue());
    }

    /**
     * 常用方法
     * add(BigDecimal)
     * subtract(BigDecimal)
     * multiply(BigDecimal)
     * divide(BigDecimal)
     * toString()
     * doubleValue()
     * floatValue()
     * longValue()
     * intValue()
     */
    @Test
    public void test02() {
        BigDecimal a = BigDecimal.valueOf(23.556);
        System.out.println("a = " + a);
        // a.longValue() = 23 小数位被截断
        System.out.println("a.longValue() = " + a.longValue());
        // 25.556
        System.out.println(a.add(new BigDecimal(2)));
        // 23.556
        System.out.println(a.doubleValue());
    }

    @Test
    public void test03() {
        double a = 0.1000000000000000123;
        double b = 0.1000000000000000124;
        // true
        System.out.println(a==b);
        // 0.1
        System.out.println(a);
        // 0.1
        System.out.println(b);
    }

    /**
     * java 中对 BigDecimal 比较大小一般用的是 BigDecimal 的
     * compareTo 方法
     */
    @Test
    public void test04() {
        BigDecimal a = BigDecimal.valueOf(0.1000000000000000123);
        BigDecimal b = BigDecimal.valueOf(0.1000000000000000124);
        // 0 ，因为在转为 BigDecimal 之前，double 的精度已经丢失了
        System.out.println(a.compareTo(b));
        // 0.1
        System.out.println(a);
        // 0.1
        System.out.println(b);
    }

    @Test
    public void test05() {
        BigDecimal a = new BigDecimal("0.1000000000000000123");
        BigDecimal b = new BigDecimal("0.1000000000000000124");
        // -1，表示 a < b
        System.out.println(a.compareTo(b));
        // 0.1000000000000000123
        System.out.println(a);
        // 0.1000000000000000124
        System.out.println(b);
    }

    @Test
    public void test06() {
        // 货币格式化引用
        NumberFormat chinaCurrent = NumberFormat.getCurrencyInstance();
        NumberFormat usCurrent = NumberFormat.getCurrencyInstance(Locale.US);
        BigDecimal loanAmount = new BigDecimal("15000.48");

        System.out.println("chinaCurrent: " + chinaCurrent.format(loanAmount)); // ￥15,000.48
        System.out.println("usCurrent: " + usCurrent.format(loanAmount)); // $15,000.48
        System.out.println("chinaCurrentn: " + chinaCurrent.format(12.34));

        NumberFormat percent = NumberFormat.getPercentInstance();
        System.out.println(percent.format(12.34)); // 1,234%

        DecimalFormat df = new DecimalFormat("#.00");
        // .30
        System.out.println(df.format(new BigDecimal("0.3")));
    }

    /**
     * BigDecimal 常见异常
     * 除法的时候出现异常
     */
    @Test
    public void test07() {
        BigDecimal a = new BigDecimal(1);
        BigDecimal b = new BigDecimal(3);
        // java.lang.ArithmeticException: Non-terminating decimal expansion; no exact representable decimal result.
        // 通过 BigDecimal 的 divide 方法进行除法时，当不能整除，出现无限循环小数时
        // 就会抛出该异常，解决方法，设置精确的小数点，如 divide(xxx, 2)
        // a.divide(b);
        System.out.println(a.divide(b, 6, RoundingMode.HALF_UP));
    }
}
