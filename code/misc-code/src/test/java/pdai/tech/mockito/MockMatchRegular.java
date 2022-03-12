package pdai.tech.mockito;

import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 参考：https://blog.csdn.net/a82514921/article/details/107969976
 * @author benfeihu
 */
@RunWith(MockitoJUnitRunner.class)
public class MockMatchRegular {
    @Mock
    List<String> testList;

    /**
     * 精确匹配，匹配指定值
     * Mockito.when(C1.test(...)).thenReturn(xxx) 等价于
     * Mockito.when(C1.test(Mockito.eq(...))).thenReturn(xxx)
     */
    @Test
    public void test01() {
        Mockito.when(testList.get(0)).thenReturn("a");
        Mockito.when(testList.get(Mockito.eq(2))).thenReturn("b");
        Assert.assertEquals("a", testList.get(0) );
        Assert.assertEquals(null, testList.get(1));
        Assert.assertEquals("b", testList.get(2));
    }

    /**
     * 类型匹配
     * any()  匹配任意类型
     * anyObject()
     * anyVararg()
     * any(Class<T> clazz)
     * anyByte()
     * anyShort()
     * anyInt()
     * anyFloat()
     * anyLong()
     * anyDouble()
     * anyString()
     * anyList()
     * anyMap()
     * anyCollection()
     * anyCollectionOf(Class<T> clazz)
     * anyIterable()
     * anyIterableOf(Class<T> clazz)
     * anySet()
     * any(Class<T> type)
     * isNull()
     * notNull()
     * isNotNull()
     */
    @Test
    public void test02() {
        Mockito.when(testList.get(Mockito.anyInt())).thenReturn("c");
        Assert.assertEquals(testList.get(0), "c");
        Assert.assertEquals(testList.get(100), "c");
    }

    /**
     * 自定义匹配器
     * <T> T argThat(ArgumentMatcher<T> matcher)
     * char charThat(ArgumentMatcher<Character> matcher)
     * boolean booleanThat(ArgumentMatcher<Boolean> matcher)
     * byte byteThat(ArgumentMatcher<Byte> matcher)
     * short shortThat(ArgumentMatcher<Short> matcher)
     * int intThat(ArgumentMatcher<Integer> matcher)
     * long longThat(ArgumentMatcher<Long> matcher)
     * float floatThat(ArgumentMatcher<Float> matcher)
     * double doubleThat(ArgumentMatcher<Double> matcher)
     */
    @Test
    public void test03() {
        Mockito.when(testList.get(Mockito.intThat(v -> v%2==0))).thenReturn("even");
        Mockito.when(testList.get(Mockito.intThat(v -> v%2!=0))).thenReturn("odd");
        Assert.assertEquals("even", testList.get(12));
        Assert.assertEquals("odd", testList.get(13));

        List<Integer> myList = Mockito.mock(List.class);
        // addAll 中的数据全为 偶数时，返回true，否则返回false
        Mockito.when(myList.addAll(Mockito.argThat(list -> list.stream().allMatch(v -> v%2==0)))).thenReturn(true);
        Assert.assertTrue(myList.addAll(Arrays.asList(2,4,6,8)));
        Assert.assertFalse(myList.addAll(Arrays.asList(2, 4, 5, 8)));

    }

    /**
     * 与指定对象为同一个对象
     */
    @Test
    public void test04() {
        Map<Object, String> map = Mockito.mock(Map.class);
        Object v1 = new Object();
        Mockito.when(map.get(Mockito.same(v1))).thenReturn("a");
        Assert.assertEquals("a", map.get(v1));
    }

    /**
     * String 类型参数支持的匹配
     */
    @Test
    public void test05() {
        Map<String, String> map = Mockito.mock(Map.class);
        // when 之前的，还是默认值
        Assert.assertEquals(null, map.get("a"));
        // 以指定字符串开头
        Mockito.when(map.get(Mockito.startsWith("a"))).thenReturn("aaa");
        Assert.assertEquals("aaa", map.get("a"));
        Assert.assertEquals("aaa", map.get("ab"));
        Assert.assertEquals(null, map.get("b"));
        // 以指定字符串结尾
        Mockito.when(map.get(Mockito.endsWith("b"))).thenReturn("b");
        Assert.assertEquals("b", map.get("b"));
        Assert.assertEquals("b", map.get("ab"));
        // 包含指定字符串
        Mockito.when(map.get(Mockito.contains("xxx"))).thenReturn("c");
        Assert.assertEquals("c", map.get("xxx"));
        Assert.assertEquals("c", map.get("axxx"));
        Assert.assertEquals("c", map.get("xxxa"));
        Assert.assertEquals("c", map.get("axxxa"));
        // 指定满足的正则表达式
        Mockito.when(map.get(Mockito.matches("[0-9]{3}"))).thenReturn("ok");
        Assert.assertEquals("ok", map.get("123"));
        Assert.assertNotEquals("ok", map.get("1a3"));
    }

}
