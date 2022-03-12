# fastjson, jackjson

引入 fastjson：
- 修改 gradle：
```gradle
compile("com.alibaba:fastjson:$fastJsonVersion") {
        force = true
    }
```
使用时引入包：`import com.alibaba.fastjson.JSON;` 。

## 解析字符串到对象

### java bean 对象

```java
public class T1 {
    @ToString
    public static class Person {
        private String name;
        private Long id;
        public void setName(String name) {
            System.out.println("--- set name ---");
            this.name = name;
        }
        public void setId(Long id) {
            this.id = id;
        }
    }
    public static void main(String[] args) {
        String s = "{\"name\":\"abcd\", \"age\": 23}";
        Person person = JSON.parseObject(s, Person.class);
        System.out.println(person);
    }
}
```
运行结果为：
```
--- set name ---
T1.Person(name=abcd, id=null)
```

### Properties

```java
import java.util.Properties;

public class T1 {
    public static void main(String[] args) {
        String s = "{\"name\":\"abcd\", \"age\": 23}";
        Properties properties = JSON.parseObject(s, Properties.class);
        System.out.println(properties);
        System.out.println(properties.getProperty("name"));
        System.out.println(properties.get("name"));
        System.out.println(properties.get("id"));
        System.out.println(properties.get("age").getClass());
    }
}
```
输出为：
```
{age=23, name=abcd}
abcd
abcd
null
class java.lang.Integer
```

### JSONObject

```java
public class T1 {
    public static void main(String[] args) {
        String s = "{\"name\":\"abcd\", \"age\": 23}";
        JSONObject parse = (JSONObject) JSON.parse(s);
        System.out.println(parse.get("name"));
    }
}
```

## jackjson

### 注意类型

先导入 jackson-databind ： `compile "com.fasterxml.jackson.core:jackson-databind:2.8.11"`。

```java
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import lombok.SneakyThrows;

import java.util.Map;

public class TestFastJson {
    @SneakyThrows
    public static void main(String[] args) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("a", 20L);
        map.put("b", 2000000000000L);
        ObjectMapper objectMapper = new ObjectMapper();
        System.out.printf("a: %s, b: %s\n", map.get("a").getClass(), map.get("b").getClass());
        String s = objectMapper.writeValueAsString(map);

        Map<String, Object> map2 = objectMapper.readValue(s, new TypeReference<Map<String, Object>>() {});
        System.out.printf("a: %s, b: %s\n", map2.get("a").getClass(), map2.get("b").getClass());
    }
}
```
输出为：
```
a: class java.lang.Long, b: class java.lang.Long
a: class java.lang.Integer, b: class java.lang.Long
```
a 因为没有超过 Integer.MAX_VALUE ，因此变为了 Integer 类型。

