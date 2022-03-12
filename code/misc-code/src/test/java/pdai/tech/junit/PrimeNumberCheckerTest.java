package pdai.tech.junit;

import com.pdai.tech.utest.PrimeNumberChecker;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)  // 第一步：指定参数运行器
public class PrimeNumberCheckerTest {

    // 第二步：声明变量
    private Integer inputNumber;
    private Boolean expectedResult;
    private PrimeNumberChecker primeNumberChecker;

    // 第三步：为测试类声明一个带有参数的公共构造函数，为变量赋值
    public PrimeNumberCheckerTest(Integer inputNumber, Boolean expectedResult) {
        this.inputNumber = inputNumber;
        this.expectedResult = expectedResult;
    }

    // 第四步：定义测试参数
    @Parameterized.Parameters  // 注解
    public static Collection<Object> primeNumbersX() { // 签名
        return Arrays.asList(
//                new Object[][] {
//                {2, true},
//                {3, true},
//                {4, false},
//                {5, true},
//                {6, false}
//        }
        );
    }

    @Before
    public void initialize() {
        primeNumberChecker = new PrimeNumberChecker();
    }

    @Test
    public void testPrimerNumberCheck() {
        System.out.println("Parameterized Number is: " + inputNumber);
        Assert.assertEquals(expectedResult, primeNumberChecker.validate(inputNumber));
    }
}
