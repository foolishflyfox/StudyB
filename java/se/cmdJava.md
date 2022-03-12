# Java 命令行编译、执行和打包

- 参考：https://www.cnblogs.com/eoalfj/p/12332808.html

在 vscode 中，默认包路径在 src 中。

## javac & java 命令

下面是一个例子，目录结构如下：
```
.
|____.vscode
| |____settings.json
|____src
| |____main
| | |____java
| | | |____com
| | | | |____fhb
| | | | | |____A.java
| | | | |____bfh
| | | | | |____A.java
```
其中 settings.json 文件是 vscode 的配置，其内容为：
```json
{
    "java.project.sourcePaths": [
        "src/main/java"
    ]
}
```
用于指定源文件的根目录为 `src/main/java`，在该目录下有两个类 com.bfh.A 和 com.fhb.A ，其中的内容分别为：
```java
package com.bfh;

public class A {
    static public void foo() {
        System.out.println(A.class.getName());
    }
}
```
和
```java
package com.fhb;

public class A {
    public static void main(String[] args) {
        com.bfh.A.foo();
    }
}
```
编译命令为：`javac -cp src/main/java -d bin src/main/java/com/fhb/A.java`，将在当前文件夹下生成 bin 文件夹，其中的目录结构为：
```
bin
|____com
| |____fhb
| | |____A.class
| |____bfh
| | |____A.class
```
执行命令：`java -cp bin com.fhb.A`，输出结果为：`com.bfh.A`。

参数 `-cp` 指定 classpath 。classpath 是 JVM 用到的一个环境变量，用于指示 JVM 如何搜索 class。

假设 classpath 是 `.:/usr/java/jdk/lib:/usr/lib`，当JVM加载 abc.xyz.Hello 类时，会依次查找:

- `<当前目录>/abc/xyz/Hello.class`
- `/usr/java/jdk/lib/abc/xyz/Hello.class`
- `/usr/lib/abc/xyz/Hello.class`

如果 JVM 在某个路径下找到了对应的 class 文件，就不再往后继续搜索。如果所有路径下都没有找到，就报错。classpath 的设定方法有两种，在系统中设置 classpath 环境变量，但是不推荐；在启动 JVM 时设置 classpath，带上参数 `-classpath` 或 `-cp` 参数即可。

## jar 包

如果有很多 .class 文件散落在各层目录中，肯定不便于管理。如果将目录打一个包，变成一个文件，就方便许多。

jar 包实际上就是这个作用。它可以把 package 组织的目录层级，以及各个目录层级下的所有文件(包括.class文件和其他文件)都打成一个 jar 文件，这样无论是备份还是分发给客户都简单许多。

jar 包实际上就是一个 zip 格式的压缩文件，而 jar 包相当于目录。如果我们要执行一个 jar 包的 class，可以把 jar 包放到 classpath 中。

还是以上面的例子为例，进入 bin 目录后，执行 `zip -r mydemo.jar com` 将生成一个 mydemo.jar。无论将该 jar 文件放到何处，进入与该文件所在目录，执行 `java -cp mydemo.jar com.fhb.Main`，将输出执行结果。

jar 还可以包含一个 特殊的 /META_INF/MANIFEST.MF 文件，该文件是纯文本，可以指定 MAIN-Class 和其他信息。JVM 会自动读取这个 MANIFEST.MF 文件，如果存在 MAIN-Class，我们就不必在命令行执行启动的类名，可以直接使用 `java -jar mydemo.jar` 。

