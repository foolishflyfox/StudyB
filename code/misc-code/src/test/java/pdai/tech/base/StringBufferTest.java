package pdai.tech.base;

import com.utils.ThreadUtils;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author benfeihu
 */
public class StringBufferTest {
    @Test
    public void test01() {
        // StringBuffer 是可变的
        StringBuffer s = new StringBuffer("abc");
        s.append("DE");
        s.insert(1, 23);
        System.out.println(s);  // a23bcDE
    }

    @Test
    public void test02() throws InterruptedException {
        // StringBuffer 是线程安全的
        StringBuffer s = new StringBuffer();
        int threadCnt = 1000;
        CountDownLatch latch = new CountDownLatch(threadCnt);
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i=0; i < threadCnt; ++i) {
            executorService.execute(() -> {
                s.append('1');
                latch.countDown();
            });
        }
        latch.await();
        executorService.shutdown();
        System.out.println(s.length()); // 1000
    }

    @Test
    public void test03() throws InterruptedException {
        StringBuffer s = new StringBuffer();
        ThreadUtils.simpleMultiThreadExecute(1000, () -> s.append('1'));
        System.out.println(s.length());
    }
}
