package com.bfh;

import com.bfh.serializable.NormalPerson;
import com.bfh.serializable.SerializableImpl;
import com.bfh.serializable.SerializablePerson;
import lombok.SneakyThrows;
import org.junit.Test;

import javax.tools.FileObject;
import java.io.*;
import java.util.Arrays;
import java.util.stream.Stream;

/**
 * @author benfeihu
 */
public class SerializableTest {

    @SneakyThrows
    private void serialize(Object obj, String file) {
        FileOutputStream fileOut = new FileOutputStream(file);
        ObjectOutputStream out = new ObjectOutputStream(fileOut);
        out.writeObject(obj);
        out.close();
    }

    @SneakyThrows
    private byte[] serialize(Object obj) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(output);
        oos.writeObject(obj);
        return output.toByteArray();
    }

    @SneakyThrows
    private <T> T deserialize(String file, Class<?> classType) {
        FileInputStream fileInputStream = new FileInputStream(file);
        ObjectInputStream in = new ObjectInputStream(fileInputStream);
        return  (T) in.readObject();
    }

    NormalPerson normalPerson = new NormalPerson("aaa", 10);
    SerializablePerson serializablePerson = new SerializablePerson("aaa", 10);
    SerializableImpl impl = new SerializableImpl(normalPerson, serializablePerson);

    /**
     * 测试 Serializable 的基本用法
     * 如果一个 Serializable 的类包含一个非 Serializable 的字段，那么在运行期会抛出 java.io.NotSerializableException 异常
     */
    @Test
    public void testNormalUsage() {
        String file = "testNormalUsage-o1.data";
        serialize(impl, file);
        SerializableImpl impl2 = deserialize(file, SerializableImpl.class);
        System.out.println(impl2);
    }

    @Test
    public void testToByteArray() {
        byte[] bytes = serialize(impl);
        String r = "";
        for (byte aByte : bytes) {
            r += String.format("%x ", aByte);
        }
        System.out.println(r);
    }

    @Test
    public void testNormalPersonSerialize() {
        // 通过调试发现，在序列化时，会判断变量是否为 Serializable 的子类，如果不是 Serializable
        // 就直接抛出错误，通过在调试时调整变量，使其跳过判断逻辑，可以实现非 Serializable 的序列化
        // 序列化的结果为：ac ed 00 05 73 72 00 21 63 6f 6d 2e 62 66 68 2e 73 65 72 69
        // 61 6c 69 7a 61 62 6c 65 2e 4e 6f 72 6d 61 6c 50 65 72 73 6f 6e 00 00 00
        // 00 00 00 00 00 00 00 00 78 70
        byte[] bytes = serialize(normalPerson);
    }
    @Test
    public void testSerializablePersonSerialize() {
        // 结果为：ac ed 00 05 73 72 00 27 63 6f 6d 2e 62 66 68 2e 73 65 72 69 61 6c
        // 69 7a 61 62 6c 65 2e 53 65 72 69 61 6c 69 7a 61 62 6c 65 50 65 72 73 6f
        // 6e 02 1c 56 46 53 06 89 74 02 00 02 4c 00 03 61 67 65 74 00 13 4c 6a 61
        // 76 61 2f 6c 61 6e 67 2f 49 6e 74 65 67 65 72 3b 4c 00 04 6e 61 6d 65 74
        // 00 12 4c 6a 61 76 61 2f 6c 61 6e 67 2f 53 74 72 69 6e 67 3b 78 70 73 72
        // 00 11 6a 61 76 61 2e 6c 61 6e 67 2e 49 6e 74 65 67 65 72 12 e2 a0 a4 f7
        // 81 87 38 02 00 01 49 00 05 76 61 6c 75 65 78 72 00 10 6a 61 76 61 2e 6c
        // 61 6e 67 2e 4e 75 6d 62 65 72 86 ac 95 1d 0b 94 e0 8b 02 00 00 78 70 00
        // 00 00 0a 74 00 03 61 61 61
        byte[] bytes = serialize(serializablePerson);
    }
}
