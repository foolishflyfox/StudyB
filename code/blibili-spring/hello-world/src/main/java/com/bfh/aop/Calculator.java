package com.bfh.aop;

/**
 * 接口不加载容器中
 * 实际上可以加 @Component 等组件注释，不过加了也不创建对象，只要这个组件是一个接口
 * 相当于告诉 Spring ioc 容器中有这种类型的组件
 * @author benfeihu
 */
public interface Calculator {

    int add(int i, int j);
    int sub(int i, int j);
    int mul(int i, int j);
    int div(int i, int j);
}
