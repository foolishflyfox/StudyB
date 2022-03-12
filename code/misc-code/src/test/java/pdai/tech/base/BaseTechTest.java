package pdai.tech.base;

import com.utils.ReflectUtils;
import org.junit.Test;

import java.util.Arrays;

public class BaseTechTest {
    @Test
    public void bufferPoolTest() {
        // java 缓存池测试

        // new Integer(123) 每次都会新建一个对象，因此不相等
        Integer newV1 = new Integer(123);
        Integer newV2 = new Integer(123);
        System.out.println("newV1 == newV2 ? " + (newV1==newV2));  // false

        // valueOf() 方法的实现比较简单，就是先判断是否在缓存池中，如果在
        // 的话就直接返回缓存池中的内容
        Integer ofV1 = Integer.valueOf(123);
        Integer ofV2 = Integer.valueOf(123);
        Integer ofV3 = Integer.valueOf("123");
        System.out.println("ofV1 == ofV2 ? " + (ofV1 == ofV2));  // true
        System.out.println("ofV1 == ofV3 ? " + (ofV1 == ofV3));  // true

        System.out.println("newV1 == ofV1 ? " + (newV1 == ofV1));  // false

        // 在java8中，Integer 缓存池的大小默认为 -128~127
        Integer ofW1 = Integer.valueOf(128);
        Integer ofW2 = Integer.valueOf(128);
        System.out.println("ofW1 == ofW2 ? " + (ofW1 == ofW2));  // false

        // 编译器会在缓冲池范围内的基本类型自动装箱过程调用 valueOf
        Integer v1 = 123;
        Integer v2 = 123;
        System.out.println("v1 == v2 ? " + (v1 == v2));  // true
        System.out.println("v1 == ofV1 ? " + (v1 == ofV1));  // true
        Integer v3 = 128;
        Integer v4 = 128;
        System.out.println("v3 == v4 ? " + (v3 == v4));  // false

        // 基本类型对应的缓冲池: boolean 的 true/false，所有的 byte 值，short -128~127，
        Boolean b1 = true;
        Boolean b2 = Boolean.valueOf(true);
        Boolean b3 = Boolean.TRUE;
        Boolean b4 = new Boolean(true);
        System.out.println("b1 == b2 ? " + (b1 == b2));  // true
        System.out.println("b2 == b3 ? " + (b2 == b3));  // true
        System.out.println("b3 == b4 ? " + (b3 == b4));  // false

        Byte byte1 = Byte.valueOf((byte) 12);
        Byte byte2 = Byte.valueOf((byte) 12);
        Byte byte3 = new Byte((byte) 12);
        System.out.println("byte1 == byte2 ? " + (byte1 == byte2));  // true
        System.out.println("byte2 == byte3 ? " + (byte2 == byte3));  // false

        Short short1 = Short.valueOf((short) 15);
        Short short2 = Short.valueOf((short) 15);
        Short short3 = new Short((short) 15);
        Short short4 = Short.valueOf((short)1024);
        Short short5 = Short.valueOf((short)1024);
        Short short6 = (short) 15;  // 自动装箱
        System.out.println("short1 == short2 ? " + (short1 == short2));  // true
        System.out.println("short2 == short3 ? " + (short2 == short3));  // false
        System.out.println("short4 == short5 ? " + (short4 == short5));  // false
        System.out.println("short1 == short6 ? " + (short1 == short6));  // true

        Character char1 = Character.valueOf('a');
        Character char2 = Character.valueOf('a');
        Character char3 = new Character('a');
        System.out.println("char1 == char2 ? " + (char1 == char2));  // true
        System.out.println("char2 == char3 ? " + (char2 == char3));  // false
    }

    @Test
    public void testString() {
        // String 被声明为 final，因此它是不可继承的
        // 内部使用 char 数组存储数据，该数组被声明为 final，这意味着 value 数组初始化后不能再引用其他数组
        // 并且 String 内部没有改变 value 数组的方法，因此保证了 String 不可变
        String s = "abc";
        System.out.println("s = " + s + ", hash = " + s.hashCode() );  // s = abc, hash = 96354
        // 通过反射机制可以修改 String 的值
        char[] value = ReflectUtils.getPrivateFieldOfObj(s, "value", char[].class);
        if (null != value ) {
            value[0] = 'A';
        }
        System.out.println(value == null ? "null" : Arrays.toString(value));
        System.out.println("s = " + s + ", hash = " + s.hashCode() );  // s = Abc, hash = 96354
        // 如果一个 String 对象已经被创建过，那么就会从 String Pool 中获取
        String t = "abc";
        System.out.println("t = " + t + ", hash = " + t.hashCode() );  // t = Abc, hash = 96354
    }

}
