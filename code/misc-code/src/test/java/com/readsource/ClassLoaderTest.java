package com.readsource;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * ClassLoader 是用来加载 Class 的，它负责将 Class 的字节码形式转换成内存形式的 Class 对象
 * 字节码可以来自磁盘的 *.class，也可以是 jar 包中的 *.class，也可以来自于远程服务器提供的字节流
 * 字节码的本质就是一个字节数组 byte[]，它有特定的复杂内存格式
 *
 * 很多字节码加密技术就是依靠定制 ClassLoader 来实现的。先用工具对字节码加密，运行时使用定制的 ClassLoader
 * 先解密文件内部内容，再加载这些解密后的字节码
 *
 * 每个 Class 对象的内部都有一个 classLoader 字段用来识别自己是由哪个 ClassLoader 加载的。
 * ClassLoader 就像一个容器，里面装了很多已经加载的 Class 对象
 *
 * JVM 内置了3个 ClassLoader：BootstrapClassLoader / ExtensionClassLoader / AppClassLoader
 *
 * 加载阶段是整个 类加载 过程中的一个阶段。在加载阶段，Java 虚拟机需要完成以下3件事情：
 * 1. 将这个字节流锁代表的全限定名来获取定义此类的二进制字节流；
 * 2. 将这个字节流所代表的静态存储结构转换为方法区的运行时数据结构；
 * 3. 在内存中生成一个代表该类的 java.lang.Class 对象，作为方法区的各种数据的访问入口
 *
 * 对于第一点，获取二进制流有如下方法：
 * a. 从 ZIP 压缩包中读取，这很常见，最终成为日后 JAR、EAR、WAR 格式的基础
 * b. 从网络获取，这种场景最典型的应用就是 Web Applet
 * c. 运行时计算生成，这种场景使用最多的就是动态代理技术，在 java.lang.reflect.Proxy 中，就是用 ProxyGenerator.generateProxyClass
 *    来为特定接口生成形式为 *$Poxy 的代理类的二进制字节流；
 * d. 由其他文件生成，典型场景是 JSP，由 JSP 文件生成对应的 Class 文件
 * e. 从数据库读取，这种场景相对少见，例如有些中间件服务器(如SAP Netweaver)可以选择把程序安装到数据库中来完成程序代码在集群间分发
 * d. 可以从加密文件中获取，这是典型的防止 class 文件被反编译的保护措施，通过加载时解密 Class 文件来保障程序运行逻辑不被窥探
 * ... ...
 * 相对于类加载过程的其他阶段，非数组类型的加载阶段是开发人员可控性最强的阶段。加载阶段既可以使用 Java 虚拟机中内置的引导类加载器来完成
 * 也可以由用户自定义的类加载器来完成，开发人员通过定义自己的类加载器去控制字节流的获取方式(重写一个类加载器的 findClass 或 loadClass 方法)
 * 实现根据自己的想法来赋予应用程序获取运行代码的动态性
 *
 */
public class ClassLoaderTest {
    static class ClassInitDemo {
        static {
            System.out.println("begin init");
        }
        static final int v1 = 10;
        static int v2 = 20;
        static void foo() {
        }
    }
    static class Son extends ClassInitDemo {
        static {
            System.out.println("son begin init");
        }
        static int v3 = 30;
    }
    @Test
    public void testGetStaticFinal() {
        ClassInitDemo classInitDemo;  // 不会输出 begin init，表示不会导致类的初始化
        int v = ClassInitDemo.v1; // 不会输出 begin init，表示不会导致类的初始化
    }
    @Test
    public void testGetStaticNormal() {
        int v = ClassInitDemo.v2;  // 会输出 begin init，表示会导致类的初始化
    }
    @Test
    public void testSetStaticNormal() {
        ClassInitDemo.v2 = 21;  // 会输出 begin init，表示会导致类的初始化
    }
    @Test
    public void testInvokeStaticMethod() {
        ClassInitDemo.foo();  // 会输出 begin init，表示会导致类的初始化
    }
    @Test
    public void testConstructor() {
        new ClassInitDemo();  // 会输出 begin init，表示会导致类的初始化
    }
    @Test
    public void testSonUseParentField() {
        // 只打印 begin init，说明如果子类只使用了父类的静态成员，不会触发子类的类初始化
        int v = Son.v2;
    }
    @Test
    public void testCreateArray() {
        // 不会触发 begin init
        ClassInitDemo[] array = new ClassInitDemo[10];
    }
    @Test
    public void testSonStaticFieldGet() {
        // 打印了 begin init, son begin init，说明如果子类使用了自己的静态成员，会导致其父类的类初始化
        int v = Son.v3;
    }

    @Test
    public void testClassLoadTypes() {
        // BootstrapClassLoader 负责加载 JVM 运行时核心类，这些类位于 JAVA_HOME/lib/rt.jar 文件中
        // 我们常用的内置库 java.xxx.* 都在这个里面，如 java.util.*, java.io.*, java.lang.* 等等。
        // 这个 ClassLoader 比较特殊，它是用 C 实现的，我们称之为根加载器
        // 因为是用 C 编写的，所以该加载器是 null
        Assert.assertNull(List.class.getClassLoader());
        Assert.assertNull(java.io.File.class.getClassLoader());
        Assert.assertNull(java.lang.Long.class.getClassLoader());
        // ExtensionClassLoader 负责加载 JVM 扩展类，比如 swing 系列，内置的 js 引擎，xml 解析器等
        // 这些类通常以 javax 开头，位于 JAVA_HOME/lib/ext/*.jar 中
        System.out.println(javax.xml.stream.EventFilter.class.getClassLoader());
        System.out.println(Thread.currentThread().getClass().getClassLoader());
    }

    @Test
    public void testDiffClassLoader() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        ClassLoader myLoader = new ClassLoader() {
            @Override
            public Class<?> loadClass(String name) throws ClassNotFoundException {
                try {
                    String fileName = name.substring(name.lastIndexOf(".")+1) + ".class";
                    InputStream is = getClass().getResourceAsStream(fileName);
                    if (is == null) {
                        return super.loadClass(name);
                    }
                    byte[] b = new byte[is.available()];
                    is.read(b);
                    return defineClass(name, b, 0, b.length);
                } catch (IOException e) {
                    throw new ClassNotFoundException(name);
                }
            }
        };
        Object obj = myLoader.loadClass("com.readsource.ClassLoaderTest").newInstance();
        Object defaultClass = this.getClass().getClassLoader().loadClass("com.readsource.ClassLoaderTest").newInstance();
        Assert.assertEquals(obj.getClass().getName(), this.getClass().getName());
        Assert.assertFalse(obj instanceof com.readsource.ClassLoaderTest);
        Assert.assertTrue(this instanceof com.readsource.ClassLoaderTest);
        Assert.assertTrue(defaultClass instanceof com.readsource.ClassLoaderTest);
    }
}
