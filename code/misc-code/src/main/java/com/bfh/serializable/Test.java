package com.bfh.serializable;

import lombok.SneakyThrows;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Test {
    @SneakyThrows
    private void serialize(Object obj, String file) {
        FileOutputStream fileOut = new FileOutputStream(file);
        ObjectOutputStream out = new ObjectOutputStream(fileOut);
        out.writeObject(obj);
        out.close();
    }

    @SneakyThrows
    private <T> T deserialize(String file, Class<?> classType) {
        FileInputStream fileInputStream = new FileInputStream(file);
        ObjectInputStream in = new ObjectInputStream(fileInputStream);
        return  (T) in.readObject();
    }

    /**
     * 测试 Serializable 的基本用法
     */
    public void testNormalUsage() {
        NormalPerson normalPerson = new NormalPerson("aaa", 10);
        SerializablePerson serializablePerson = new SerializablePerson("bbb", 20);
        SerializableImpl impl = new SerializableImpl(normalPerson, serializablePerson);
        String file = "testNormalUsage-o1.data";
        serialize(impl, file);
        SerializableImpl impl2 = deserialize(file, SerializableImpl.class);
        System.out.println(impl2);
    }
}
