package com.bfh.config;

import com.bfh.bean.Color;
import org.springframework.beans.factory.FactoryBean;

/**
 * 创建一个 Spring 定义的 FactoryBean
 * @author benfeihu
 */
public class ColorFactoryBean implements FactoryBean <Color> {

    public ColorFactoryBean() {
        System.out.println("######");
    }

    @Override
    public Color getObject() throws Exception {
        System.out.println("ColorFactoryBean getObject");
        return new Color();
    }

    @Override
    public Class<?> getObjectType() {
        return Color.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
