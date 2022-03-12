package pdai.tech.junit;

import com.utils.DateTimeUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

public class JUnitTimeoutTest {

    @Test(timeout = 500)
    public void testTimeout() {
        DateTimeUtils.sleep(498);  // 498 测试没通过，500 测试不通过
    }

    @Rule
    public Timeout globalTimeout = Timeout.millis(100);

    @Test
    public void globalTimeout() {
        DateTimeUtils.sleep(50);
        System.out.println("end of globalTimeout");
    }

}
