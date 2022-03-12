package pdai.tech.log;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;

/**
 * @author benfeihu
 */
public class LogDemo {
    @Test
    public void test01() {
        Logger log = LoggerFactory.getLogger(LogDemo.class);
        log.trace("benfeihu trace");
        log.info("benfeihu info");

    }
}
