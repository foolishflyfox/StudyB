# MVEL

## 参考

![MVEL 2.x语法指南](https://bigjun2017.github.io/2018/09/18/hou-duan/java/mvel2.x-yu-fa-zhi-nan/)

## 单参数执行 eval

```java
public class T1 {
    public static void main(String[] args) {
        // 解释执行: 36
        System.out.println(MVEL.eval("6 * 6"));
        // 编译执行: 36
        System.out.println(MVEL.executeExpression(MVEL.compileExpression("6 * 6")));

        // 可以执行语句
        MVEL.eval("System.out.println('hello,world')");
        // 必须引入 com.google.guava: [a, b]
        System.out.println(MVEL.eval("com.google.common.collect.Lists.newArrayList('a', 'b')"));

        // 最后一条语句的执行结果为输出: 3
        System.out.println(MVEL.eval("1;2;3"));
        // mvel 可以自动识别类型: class java.lang.Integer
        System.out.println(MVEL.eval("1").getClass());
        // class java.lang.String
        System.out.println(MVEL.eval("'1'").getClass());
        // null
        System.out.println(MVEL.eval(""));
        // class java.util.ArrayList
        System.out.println(MVEL.eval("['a', 'b']").getClass());
        // class java.util.HashMap
        System.out.println(MVEL.eval("['a':1, 'b':2]").getClass());
        // class [Ljava.lang.Object
        System.out.println(MVEL.eval("{1, 2, 3}").getClass());
    }
}
```

## 传入 context
```java
package com.bfh;

import org.mvel2.MVEL;

import java.util.HashMap;
import java.util.Map;

/**
 * @author benfeihu
 */
public class T2 {
    static class P1 {
        private String name;
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public P1(String name) { this.name = name; }
    }
    static class P2 {
        // mvel 需要获取 name，但 name 为 public，因此报错 IllegalAccessException
        // Class org.mvel2.PropertyAccessor can not access a member of class com.bfh.T1$P2 with modifiers "public"
        public String name;
        // public String getName() {return name;}
        public P2(String name) { this.name = name; }
    }
    static class P3 {
        public String getName() { return "P3_name"; }
    }

    static class P4 {

    }

    public static void main(String[] args) {
        P1 p1 = new P1("P1_name");
        System.out.println(MVEL.eval("name", p1));  // 通过 getName 获取到 P1_name
        // MVEL.eval("name='new value'", p1); // 不能被设置
        System.out.println(p1);
        // System.out.println(MVEL.eval("name", new P2("P2_name")));  // 报错：IllegalAccessException
        System.out.println(MVEL.eval("name", new P3()));  // 只需要提供 getName() 方法即可

        Map<String, Object> map = new HashMap<>();
        map.put("name", "map['name']");
        System.out.println(MVEL.eval("name", map));  // 可以是 map 类，其中的 key 为 name
        MVEL.eval("name='new value'", map);  // 可以被设置
        System.out.println(map);  // {name=new value}

        HashMap<String, Object> tmp = new HashMap<>();
        // 285
        System.out.println(MVEL.eval("a = 0; for(int b = 0; b < 10; ++b) { a+=b*b; } a", tmp));
        // context 中的 a 被赋值，b 因为是临时变量，未被赋值
        System.out.println(tmp);

        // 非 map 不能被赋值
        // System.out.println(MVEL.eval("int a=0; for(int b = 0; b < 10; ++b) { a+=b*b; } a", new P4()));

    }
    public static void printNameFromCtx(Object ctx) {
        System.out.println(MVEL.eval("name", ctx));
    }
}
```

## 注意点

### 数值比较

```java
public class T1 {
    public static void main(String[] args) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("a", 1000);
        map.put("b", 123456789123456789L);
        map.put("c", 1000L);
        System.out.printf("a = %s, b = %s, c = %s\n", map.get("a").getClass(), map.get("b").getClass(),
                map.get("c").getClass());
        execute("a==1000", map);
        execute("a==1000L", map);
        execute("a=='1000'", map);
        execute("a=='1000L'", map);
        execute("['1000'] contains a.toString()", map);
        execute("[1000] contains a", map);
        execute("[1000L] contains a", map);

        execute("b==123456789123456789", map);
        execute("b==123456789123456789L", map);
        execute("b=='123456789123456789'", map);
        execute("b=='123456789123456789L'", map);
        execute("['123456789123456789'] contains b.toString()", map);
        execute("[123456789123456789] contains b", map);
        execute("[123456789123456789L] contains b", map);

        execute("c==1000", map);
        execute("c==1000L", map);
        execute("c=='1000'", map);
        execute("c=='1000L'", map);
        execute("['1000'] contains c.toString()", map);
        execute("[1000] contains c", map);
        execute("[1000L] contains c", map);
    }

    static void execute(String exp, Map<String, Object> ctx) {
        System.out.printf("\"%s\":\t%s\n", exp, MVEL.eval(exp, ctx, Boolean.class));
    }
}
```
执行结果为：
```
a = class java.lang.Integer, b = class java.lang.Long, c = class java.lang.Long
"a==1000":	true
"a==1000L":	true
"a=='1000'":	true
"a=='1000L'":	false
"['1000'] contains a.toString()":	true
"[1000] contains a":	true
"[1000L] contains a":	false
"b==123456789123456789":	true
"b==123456789123456789L":	true
"b=='123456789123456789'":	true
"b=='123456789123456789L'":	false
"['123456789123456789'] contains b.toString()":	true
"[123456789123456789] contains b":	true
"[123456789123456789L] contains b":	true
"c==1000":	true
"c==1000L":	true
"c=='1000'":	true
"c=='1000L'":	false
"['1000'] contains c.toString()":	true
"[1000] contains c":	false
"[1000L] contains c":	true
```
结论：
- 数值 `==` 运算可以直接比较；
- 如果用 `[数值] contains 变量`，可以分为以下几种情况
    1. 数值超过 int 最大值，变量为 Long，则数值可以不加 L 后缀；
    2. 数值不超过 int 最大值，变量为 Long，则数值必须加 L 后缀；
    3. 数值不超过 int 最大值，变量为 Integer，则数值必须不加 L 后缀；
    4. 结论，contains 都转成 String 进行比较，以避免上述差异；

