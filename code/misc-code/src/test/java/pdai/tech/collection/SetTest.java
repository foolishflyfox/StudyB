package pdai.tech.collection;

import com.utils.ThreadUtils;
import org.junit.Test;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class SetTest {
    @Test
    public void test01() {
        foo(new TreeSet<>());  // 1 2 5 7 100 有序, 有 subset 获取子集合
        foo(new HashSet<>());  // 1 2 100 5 7 无序，查找效率高
        foo(new LinkedHashSet<>());  // 5 2 7 1 100 , 遍历顺序为插入顺序
    }
    private void foo(Set<Integer> set) {
        set.add(5);
        set.add(2);
        set.add(7);
        set.add(1);
        set.add(100);
        System.out.println(set.stream().map(String::valueOf)
                .collect(Collectors.joining(" ")));
    }

    @Test
    public void testTreeSetThreadSafe() throws InterruptedException {
         Set<Integer> set = new TreeSet<>();  // 非线程安全，会抛错
//        Set<Integer> set = new CopyOnWriteArraySet<>();
        AtomicInteger v = new AtomicInteger(0);
        ThreadUtils.simpleMultiThreadExecute(1000, () -> set.add(v.getAndIncrement()));
        System.out.println(set.size());
    }

    @Test
    public void testHashSetThreadSafe() throws InterruptedException {
        Set<Integer> set = new HashSet<>();  // 非线程安全，不会抛错，但传入的数据量不对
        AtomicInteger v = new AtomicInteger(0);
        ThreadUtils.simpleMultiThreadExecute(1000, () -> set.add(v.getAndIncrement()));
        System.out.println(set.size());
    }

    @Test
    public void testLinkedHashSetThreadSafe() throws InterruptedException {
        Set<Integer> set = new LinkedHashSet<>();  // 非线程安全，不会抛错，但传入的数据量不对
        AtomicInteger v = new AtomicInteger(0);
        ThreadUtils.simpleMultiThreadExecute(1000, () -> set.add(v.getAndIncrement()));
        System.out.println(set.size());
    }

    @Test
    public void testCopyOnWriteArraySet() throws InterruptedException {
        Set<Integer> set = new CopyOnWriteArraySet<>();  // 线程安全
        AtomicInteger v = new AtomicInteger(0);
        ThreadUtils.simpleMultiThreadExecute(1000, () -> set.add(v.getAndIncrement()));
        System.out.println(set.size());
    }
}
