package pdai.tech.base;

import lombok.Data;
import lombok.SneakyThrows;
import org.junit.Test;

/**
 * 使用 clone 方法来拷贝一个对象即复杂，又有风险，并且还需要类型转换。
 * Effective Java 建议最好不要去使用 clone() ，可以使用拷贝构造函数或者拷贝工厂来拷贝一个对象
 * @author benfeihu
 */
public class CloneableTest {
    class A {
        private Integer v;

        @Override
        protected A clone() throws CloneNotSupportedException {
            return (A) super.clone();
        }
    }
    class B implements Cloneable {
        private Integer v;

        @Override
        protected B clone() throws CloneNotSupportedException {
            return (B) super.clone();
        }
    }


    @SneakyThrows
    @Test
    public void test01() {
        A a1 = new A();
        A a2 = a1.clone();  // 报错，因为 A 没有 implement Cloneable
        System.out.println(a2);
    }

    @SneakyThrows
    @Test
    public void test02() {
        B b1 = new B();
        B b2 = b1.clone();
        System.out.println(b2);
    }
}
