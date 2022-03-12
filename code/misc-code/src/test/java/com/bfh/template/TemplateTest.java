package com.bfh.template;

import org.junit.Test;

import java.util.function.Consumer;

/**
 * @author benfeihu
 */
public class TemplateTest {
    @Test
    public void test01() {
        class A {}
        class B extends A {}
        class C {}
        class Foo <E extends A> {
            private E e;
            public Foo(E e) {
                this.e = e;
            }
            public void execute() {
                System.out.println(e.getClass().getName());
            }
        }
        // 虽然模板参数 FOO 中的模板参数为 ? , 但并不表示可以是任意类型，因为
        // Foo 定义的时候指定了其模板参数为 extends A
        // 因此 consumer.accept(new Foo<>(new A())), consumer.accept(new Foo<>(new B())) 不会报错
        // consumer.accept(new Foo<>(new C())) 会报错
        Consumer<Foo<?>> consumer = Foo::execute;
        consumer.accept(new Foo<>(new B()));
    }
}
