package com.bfh.bean.work;

import com.bfh.bean.work.Car;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

// 默认加在 ioc 容器中的组件，容器启动会调用无参构造器创建对象，再进行初始化操作
@Component
@Data
public class Boss {
    private Car car;

    @Autowired  // 只有一个有参构造器，@Autowired 可以省略
    public Boss(Car car) {
        System.out.println("Boss constructor");
        this.car = car;
    }
    public Boss() {}

    // @Autowired 标注在方法上，Spring 容器创建当前对象，就会调用方法完成赋值
    // 方法使用的参数，注定难以类型的值从 ioc 容器中获取
//    @Autowired
    public void setCar(Car car) {
        this.car = car;
    }
}
