package com.bfh;

import com.bfh.bean.Car;
import com.bfh.bean.Family;
import com.bfh.bean.Pet;
import com.bfh.bean.User;
import com.bfh.config.MyConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;
import java.util.List;

/**
 * @author benfeihu
 * @SpringBootApplication(scanBasePackage="com") 可以改变包扫描路径
 * @ComponentScan 也可以指定
 */
@SpringBootApplication
public class MainApp {
    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(MainApp.class, args);
        Family bean = run.getBean(Family.class);
        System.out.println(bean);
        // 返回 ioc 容器
//        ConfigurableApplicationContext run = SpringApplication.run(MainApp.class, args);
//        List<String> names = Arrays.asList(run.getBeanDefinitionNames());
//        System.out.println(names.contains("user01"));
//        System.out.println(names.contains("tom"));
//        for (String name : names) {
//            System.out.println(name);
//        }
        // 组件默认是单实例的
//        Pet tom01 = run.getBean("tom", Pet.class);
//        Pet tom02 = run.getBean("tom", Pet.class);
//        System.out.println("tom01 = " + tom01 + ", tom02 = " + tom02);
//        // 配置类本身也是组件，获得的是代理对象 com.bfh.config.MyConfig$$EnhancerBySpringCGLIB$$4e5d0431@65e7f52a
//        MyConfig myConfig = run.getBean(MyConfig.class);
//        System.out.println(myConfig);

        // 如果 @Configuration(proxyBeanMethod = true) 代理对象调用方法
        // springboot 总会保持单实例
//        User userA = myConfig.user01();
//        User userB = myConfig.user01();
//        System.out.println(userA == userB);

//        System.out.println("=======");
//        String[] userNames = run.getBeanNamesForType(User.class);
//        for (String userName : userNames) {
//            System.out.println(userName);
//        }
//        System.out.println(run.getBean(User.class));

//        System.out.println(run.getBean(Pet.class));
//        System.out.println(run.containsBean("tom"));
//        System.out.println(run.containsBean("user01"));

    }
    @Bean
    public Car car() {
        return new Car("kitty", 123);
    }

    @Bean
    public User user() {
        return new User("zhangsan", 23, new Pet("aaa"));
    }
}
