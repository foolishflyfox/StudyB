package pdai.tech.base;

import com.utils.ThreadUtils;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author benfeihu
 */
public class StringBuilderTest {
    @Test
    public void test01() {
        // StringBuilder 是可变的
        StringBuilder s = new StringBuilder("abc");
        s.append("DE");
        s.insert(1, 23);
        System.out.println(s);  // a23bcDE
    }

    @Test
    public void test02() throws InterruptedException {
        // StringBuilder 不是线程安全的
        StringBuilder s = new StringBuilder();
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
        System.out.println(s.length()); // 运行结果已确定，如 977、972、982，都小于 1000
    }

    @Test
    public void test03() throws InterruptedException {
        StringBuilder s = new StringBuilder();
        ThreadUtils.simpleMultiThreadExecute(1000, () -> s.append('1'));
        System.out.println(s.length());
    }
}
