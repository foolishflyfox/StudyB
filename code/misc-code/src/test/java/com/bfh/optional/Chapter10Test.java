package com.bfh.optional;

import com.bfh.optional.v1.Car;
import com.bfh.optional.v1.Insurance;
import com.bfh.optional.v1.Person;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;
import java.util.OptionalInt;
import java.util.Properties;

/**
 * @author benfeihu
 */
public class Chapter10Test {
    public String getCarInsuranceName0(Person person) {
        return person.getCar().getInsurance().getName();
    }
    // 采用防御式检查减少 NPE
    public String getCarInsuranceName1(Person person) {
        if (null != person) {
            Car car = person.getCar();
            if (null != car) {
                Insurance insurance = car.getInsurance();
                if (null != insurance) {
                    return insurance.getName();
                }
            }
        }
        return "Unknown";
    }

    // 减少 if 嵌套，但是增加了退出点的数量，不容易维护
    public String getCarInsuranceName2(Person person) {
        String unknow = "Unknow";
        if (null == person) {
            return unknow;
        }
        if (null == person.getCar()) {
            return unknow;
        }
        if (null == person.getCar().getInsurance()) {
            return unknow;
        }
        return person.getCar().getInsurance().getName();
    }

    // 使用 Optional 获取
    public String getCarInsuranceName3(Optional<com.bfh.optional.v2.Person> person) {
        return person.flatMap(com.bfh.optional.v2.Person::getCar)
                .flatMap(com.bfh.optional.v2.Car::getInsurance)
                .map(com.bfh.optional.v2.Insurance::getName)
                .orElse("Unknown");
    }

    public String getCarInsuranceName(Optional<com.bfh.optional.v2.Person> person, int minAge) {
        return person.filter(p -> p.getAge()>=minAge)
                .flatMap(com.bfh.optional.v2.Person::getCar)
                .flatMap(com.bfh.optional.v2.Car::getInsurance)
                .map(com.bfh.optional.v2.Insurance::getName)
                .orElse("Unknown");
    }

    @Test
    public void test01() {
        Integer v = null;
        // of 会报错，因为 null 作为一个正常值
         Optional<Integer> ov = Optional.of(v);
        //不会报错，因为 null 会导致返回 Optional.empty()
//        Optional<Integer> ov = Optional.ofNullable(v);
//        System.out.println(ov.map(t -> t * 2).orElse(-1));
    }

    // 测试 flatMap 是否可以解印出任意多层的 Optional
    @Test
    public void test02() {
//        Optional<Optional<Optional<Optional<Integer>>>> v =
//                Optional.of(Optional.of(Optional.of(Optional.of(123))));
        class V {
            @Getter
            private Optional<Integer> r;
            public V(Integer r) {
                this.r = Optional.of(r);
            }
        }
        class U {
            @Getter
//            private Optional<Optional<V>> v;
            private Optional<V> v;
            public U(Integer r) {
//                v = Optional.of(Optional.of(new V(r)));
                v = Optional.of(new V(r));
            }
        }
        class T {
            @Getter
            private Optional<U> u;
            public T(Integer r) {
                u = Optional.of(new U(r));
            }
        }
        Optional<T> t = Optional.of(new T(23));
        System.out.println(t.flatMap(T::getU).flatMap(U::getV).flatMap(V::getR).isPresent());
        // Optional 的 map 操作保留外层的 Optional
        Optional<Integer> integer = Optional.of(12).map(v -> v * 2);
        // 因为 t 是 Optional，因此 map 一定带有一个 Optional，而在 t 中的 u 也是一个 Optional
        // 因此返回结果为 Optional<Optional<U>>
        Optional<Optional<U>> u = t.map(T::getU);
        // flat map 会将两层 Optional 合为一层 Optional
        Optional<U> u1 = t.flatMap(T::getU);

    }

    @Test
    public void test03() {
        System.out.println(foo(Optional.of(new A(2)), Optional.of(new B(3))));
    }

    @Test
    public void test04() {
        Integer v = null;
        Optional.of(v);
    }

    @AllArgsConstructor
    static class A {
        @Getter
        private Integer v;
    }
    @AllArgsConstructor
    static class B {
        @Getter
        private Integer v;
    }
    Optional<Integer> foo(Optional<A> a, Optional<B> b) {
        return a.flatMap(ta -> b.map(tb -> foo(ta, tb)));
    }
    Integer foo(A a, B b) {
        return a.getV() + b.getV();
    }

    @Test
    public void test05() {
        int i = Integer.parseInt("666");
        System.out.println(i);
    }

    @Test
    public void test06() {
        Properties props = new Properties();
        props.setProperty("a", "5");
        props.setProperty("b", "true");
        props.setProperty("c", "-3");
        Assert.assertEquals(5, readDuration(props, "a"));
        Assert.assertEquals(0, readDuration(props, "b"));
        Assert.assertEquals(0, readDuration(props, "c"));
        Assert.assertEquals(0, readDuration(props, "d"));

    }
    public int readDuration(Properties props, String name) {
        return Optional.ofNullable(props.getProperty(name))
                .flatMap(this::stringParseToInt)
                .filter(v -> v > 0)
                .orElse(0);
    }
    Optional<Integer> stringParseToInt(String s) {
        try {
            return Optional.of(Integer.parseInt(s));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

}
