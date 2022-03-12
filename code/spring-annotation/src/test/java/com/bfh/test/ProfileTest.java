package com.bfh.test;

import com.bfh.bean.Yellow;
import com.bfh.config.MainConfigOfProfile;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javax.sql.DataSource;
import java.util.Arrays;

public class ProfileTest {

    // 使用参数
    @Test
    public void test01() {
        // 1. 创建一个 applicationContext
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        // 2. 设置需要激活的环境
        context.getEnvironment().setActiveProfiles("prod");
        // 3. 注册主配置类
        context.register(MainConfigOfProfile.class);
        // 4. 刷新容器
        context.refresh();
        System.out.println(Arrays.toString(context.getBeanNamesForType(DataSource.class)));
        System.out.println(context.getBean(Yellow.class));
    }
}
