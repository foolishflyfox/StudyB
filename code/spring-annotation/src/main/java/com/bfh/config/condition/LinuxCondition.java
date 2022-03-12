package com.bfh.config.condition;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class LinuxCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
//        context.getBeanFactory();  // 获取 bean 工厂
        context.getClassLoader();  // 获取类加载器
        context.getRegistry();  // 获取 bean 定义的注册类
        return context.getEnvironment().getProperty("os.name").contains("Linux");
    }
}
