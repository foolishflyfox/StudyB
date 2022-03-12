package com.myspring;

/**
 * @author benfeihu
 */
public class MySpringApplicationContext {

    private Class<?> configClass;

    public MySpringApplicationContext(Class<?> configClass) {
        this.configClass = configClass;
    }

    public Object getBean(String beanName) {
        return null;
    }
}
