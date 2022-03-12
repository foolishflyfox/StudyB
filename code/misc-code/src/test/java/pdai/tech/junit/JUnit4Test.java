package pdai.tech.junit;

import org.junit.*;

/**
 * @author benfeihu
 */
public class JUnit4Test {
    @BeforeClass
    public static void beforeClassAction() {
        System.out.println("before class Action");
    }
    @Before
    public void beforeAction() {
        System.out.println("before Action");
    }
    @After
    public void afterAction() {
        System.out.println("after Action");
    }
    @Test
    public void test01() {
        System.out.println("test01");
    }
    @Test
    public void test02() {
        System.out.println("test02");
    }
    @Test
    @Ignore
    public void test03() {
        System.out.println("test03");
    }

    @AfterClass
    public static void afterClassAction() {
        System.out.println("after class Action");
    }

}
