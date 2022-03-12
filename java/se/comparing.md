# Comparator.comparing 函数分析

我们有一个对象 Person，其定义为：
```java
@AllArgsConstructor
@ToString
@Getter
static class Person {
    int age;
    int id;
    String name;
}
```
创建一个 Person 的数组：
```java
List<Person> ps = Lists.newArrayList(
        new Person(10, 1, "apple"),
        new Person(5, 2, "orange"),
        new Person(15, 3, "banana")
);
```
按 age 对该数组进行排序: `ps.sort((p1, p2) -> p1.getAge()-p2.getAge());`
按 id 对数组进行排序：`ps.sort((p1, p2) -> p1.getId()-p2.getId());`
按 name 对数组进行排序：`ps.sort((p1, p2) -> p1.getName().compareTo(p2.getName()));`

如果使用 Compartor.comparing ，则上面3行可以改写为：
- `ps.sort(Comparator.comparing(Person::getAge));`
- `ps.sort(Comparator.comparing(Person::getId));`
- `ps.sort(Comparator.comparing(Person::getName));`

改写后的含义更加清晰。

## 分析

sort 函数需要的是一个Comparator接口，Comparator 中有一个比较函数 `int compare(T o1, T o2)`。因此，Comparator.comparing 返回的就应该是一个 Comparator。下面我们尝试自己定义一个 comparing：
```java
static <T, U extends Comparable> Comparator<? super T> myComparing(
        // 传入的是从 T 到 U 的转换函数
        Function<? super T, ? extends U> keyExactor) {
    return (T o1, T o2) -> keyExactor.apply(o1).compareTo(keyExactor.apply(o2));
}
```
则上面的比较函数的生产可以将 `Comparator.comparing` 改为 `myComparing`。


