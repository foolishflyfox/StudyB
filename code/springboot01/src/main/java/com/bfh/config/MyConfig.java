package com.bfh.config;

import ch.qos.logback.core.db.DBHelper;
import com.bfh.bean.Car;
import com.bfh.bean.Pet;
import com.bfh.bean.User;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;
import org.springframework.core.annotation.Order;

/**
 * @Configuration(proxyBeanMethods = true)
 *      配置类组件之间无依赖关系，用 lite 模式(false)，加速容器启动过程，减少判断
 *      配置类组件之间有依赖关系，用 full 模式(true)
 * @author benfeihu
 */
@Import({DBHelper.class})  // 通过无参构造器创建 bean 组件放入容器中
@Configuration(proxyBeanMethods = true)
//@EnableConfigurationProperties(Car.class)
public class MyConfig {

    /**
     * 对于有原来关系的组件，被依赖组件要写在前面
     * @return
     */
//    @Bean("tom")
//    public Pet tomcat() {
//        return new Pet("TomCat");
//    }

    /**
     * 给容器中添加组件，以方法名作为组件 id，返回类型就是组件类型，方法返回的值就是组件在容器中的实例
     * 外部无论对配置类的这个组件注册方法调用多少次，获取的都是之前注册容器中的单实例对象
     */
//    @ConditionalOnBean(name={"tom"})
//    @Bean
//    public User user01() {
//        return new User("zhangsan", 18, tomcat());
//    }

}
