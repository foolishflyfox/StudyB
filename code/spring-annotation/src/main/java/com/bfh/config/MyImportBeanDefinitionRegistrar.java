package com.bfh.config;

import com.bfh.bean.Rainbow;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author benfeihu
 */
public class MyImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {
    /**
     *
     * @param importingClassMetadata 当前类的注解信息
     * @param registry BeanDefinition 注册类
     */
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        // 手动注册
        boolean hasRed = registry.containsBeanDefinition("com.bfh.bean.Blue");
        boolean hasBlue = registry.containsBeanDefinition("com.bfh.bean.Yellow");
        // 指定 bean 名
        if (hasRed && hasBlue) {
            // 指定 bean 定义信息，包括 bean 类型、scope 等
            RootBeanDefinition beanDefinition = new RootBeanDefinition(Rainbow.class);
            // 注册一个 bean ，指定 bean 名
            registry.registerBeanDefinition("rainBow", beanDefinition);
        }
    }
}
