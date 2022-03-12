package com.bfh.serialize;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * @author benfeihu
 */
public class SerializeTest {
    @Test
    public void test01() {
        A a = new A(1,2L);

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(buffer);
            oos.writeObject(a);
            System.out.println(buffer.size());
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @AllArgsConstructor
    @NoArgsConstructor
    static class A implements Serializable {
        private int v1;
        private Long v2;
    }
}
