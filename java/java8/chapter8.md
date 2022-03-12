# 为改善可读性和灵活性重构代码

## 代码重构

### 从匿名类到 Lambda 表达式的转换

将实现单一抽象方法的匿名类转换为 Lambda 表达式。因为匿名类是及其繁琐且容易出错的。采用 lambda 表达式之后，你的代码会更简洁，可读性更好。但某些情况下，将你们表达式之后为 Lambda 表达式中，this 和 super 的含义是不同的。

1. 在匿名类中，this 代表的是类自身，但在 Lambda 中，它代表的是包含类；
2. 匿名类可以屏蔽包含类的变量，而lambda表达式不能，会导致编译错误；
3. lambda 表达式可能存在含义模糊的情况，可以通过类型强转解决；

### 从 Lambda 表达式到方法引用的转换

Lambda 表达式非常适合需要传递代码片段的场景。不过，为了改善代码的可读性，尽量使用方法引用。因为方法名往往能更直观地表达代码的意图。例如，第六章我们曾经展示过下面这段代码：
```java
Map<CaloricLevel, List<Dish>> dishesByCaloricLevel = 
    menu.stream().collect(groupingBy(dish -> {
        if(dish.getCalories() <= 400) return CaloricLevel.DIET;
        else if(dish.getCalories() <= 700) return CaloricLevel.NORMAL;
        else return CaloricLevel.FAT;
    }))
```
你可以将 Lambda 表达式的内容抽取到一个单独的方法中，将其作为参数传递给 groupingBy 方法：
```java
Map<CaloricLevel, List<Dish>> dishesByCaloricLevel = 
    menu.stream().collect(groupingBy(Dish::getCaloricLevel));
```
为了实现这个方案，你还要在 Dish 类中添加方法：
```java
public class Dish {
    ...
    public CaloricLevel getCaloricLevel() {
        f(getCalories() <= 400) return CaloricLevel.DIET;
        else if(getCalories() <= 700) return CaloricLevel.NORMAL;
        else return CaloricLevel.FAT;
    }
}
```
除此之外，我们还应该尽量考虑使用静态辅助方法，比如 `comparing`、`maxBy`。这些方法设计之初就考虑了会结合方法引用一起使用。
