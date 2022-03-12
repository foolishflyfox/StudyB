```java
    public static void main(String[] args) {
        ApplicationContext ioc = new ClassPathXmlApplicationContext("ioc2.xml");
        Object bean = ioc.getBean("person3");
        System.out.println(bean);
    }
```

调用栈：

- ClassPathXmlApplicationContext(String configLocation) -> ClassPathXmlApplicationContext(new String[]{configLocation}, true, null)
    - ClassPathXmlApplicationContext(String[] configLocations, boolean refresh, ApplicationContext parent) -> refresh()
        - prepareRefresh() 为 refresh 做准备，包括记录启动时间


Spring 的 ApplicationContext 的函数：
- getStartupDate()：获取 spring 容器启动时间；
- isActive()：获取容器是否可用；
- toString(): 返回关于该容器的信息，包括启动时间，如果有 parent context，将打印 parent context 的 displayName；


## 函数功能

- ClassPathXmlApplicationContext(String[] configLocations, boolean refresh, ApplicationContext parent)
    - configLocations： 资源路径数组
    - refresh：在载入资源路径后，是否自动刷新 context，载入所有 bean 定义，创建所有单例；
    - parent：父 context


## 技术总结

### Spring 中的同步机制

1. 新建一个对象，例如：`private final Object activeMonitor = new Object();`
2. 所有想要该锁同步的地方，添加：`synchronized (this.activeMonitor) { 具体操作 }`
