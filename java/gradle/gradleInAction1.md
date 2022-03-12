# 第一章 项目自动化
 
## ant 简介

一个 ant 的简单例子。目录结构为：
```
├── build.xml
└── src
    └── com
        └── bfh
            └── Main.java
```
Main.java 的内容为：
```java
package com.bfh;

public class Main {
    public static void main(String[] args) {
        System.out.println("hello, ant.");
    }
}
```
build.xml 的内容为：
```xml
<project name="my-app" default="dist" basedir=".">
    <property name="src" location="src"/>
    <property name="build" location="build"/>
    <property name="dist" location="dist"/>
    <property name="version" value="1.0"/>

    <target name="init">
        <mkdir dir="${build}"/>
    </target>

    <target name="compile" depends="init" description="compile the source">
        <javac srcdir="${src}" destdir="${build}" includeantruntime="false"/>
    </target>

    <target name="dist" depends="compile" description="generate the distribution">
        <mkdir dir="${dist}"/>
        <jar jarfile="${dist}/my-app-${version}.jar" basedir="${build}"/>
    </target>

    <target name="clean">
        <delete dir="${build}"/>
        <delete dir="${dist}"/>
    </target>
</project>
```
执行 `ant`，输出为：
```
Buildfile: /Users/foolishflyfox/Code/Year2021/Mon11/day29/my-app/build.xml

init:
    [mkdir] Created dir: /Users/foolishflyfox/Code/Year2021/Mon11/day29/my-app/build

compile:
    [javac] Compiling 1 source file to /Users/foolishflyfox/Code/Year2021/Mon11/day29/my-app/build

dist:
    [mkdir] Created dir: /Users/foolishflyfox/Code/Year2021/Mon11/day29/my-app/dist
      [jar] Building jar: /Users/foolishflyfox/Code/Year2021/Mon11/day29/my-app/dist/my-app-1.0.jar

BUILD SUCCESSFUL
Total time: 0 seconds
```
查看目录结构为：
```
├── build
│   └── com
│       └── bfh
│           └── Main.class
├── build.xml
├── dist
│   └── my-app-1.0.jar
└── src
    └── com
        └── bfh
            └── Main.java
```

## Maven

Maven 选择了约定优于配置的思想，这意味着它为你的项目配置和行为提供有意义的默认值。项目自然知道去哪些目录寻找源代码，以及构建运行时有哪些 task 去执行。如果你的项目遵循默认值，那只需要写几行 XML 就可以建立一个完整的项目。另外，Maven 也拥有为应用产生包含 JavaDoc 在内的 HTML 格式项目文档的能力。

Maven 的核心功能可以通过开发定制的插件来扩展。Maven 社区非常活跃，几乎支持构建的每个方面，从集成其他工具到报告生成，你能够找到合适的插件，如果没有，可以自己写一个，

## 标准的目录布局

通过引入一个默认的项目布局，Maven 确保每个拥有 Maven 知识的开发人员可以立刻知道去哪里找什么类型的文件。例如，Java 应用程序源代码的目录是 src/main/java。所有默认的目录都是可配置的。下面是一个 Maven 项目的默认布局。
```
├── src
│   ├── main
│   │   ├── java            // java 应用程序源代码
│   │   └── resources       // 应用程序资源文件
│   └── test
│       ├── java            // Java 测试源代码
│       └── resources       // 测试资源文件
└── target                  // 由构建生成的输出目录，如 class 文件
```

构建生命周期：Maven 基于构建生命周期的思想。每个项目都确切知道有哪些步骤去执行构建、打包和发布应用程序，包括如下功能：
- 编译源代码
- 运行单元测试和集成测试
- 组装工件(例如 JAR 文件)
- 将工件部署到本地仓库
- 将构建发布到远程仓库

在 Maven 项目中，所需要的外部依赖库都在构建脚本中定义。例如，如果项目需要 Hibernate，那么你可以在依赖配置块中简单地滴定仪它的独立工件坐标，比如组织名、工件名和版本。例如：
```xml
<dependencies>
    <dependency>
        <groupId>org.hibernate</groupId>
        <artifactId>hibernate-core</artifactId>
        <version>4.1.7.Final</version>
    </dependency>
</dependencies>
```
运行时，声明的类库和它们的传递依赖会由 Maven 的依赖管理器下载，保存在本地缓存中，这样你的构建就可以使用它们。Maven 预配置从 Maven Central 下载依赖。接下来构建会从本地缓存中重用已存在的工件，因此不用再连 Maven Central。Maven Central 是 Java 社区最流行的二进制工件仓库。

Maven 中的依赖管理不仅限于外部库。你也可以将其他 Maven 项目定义为依赖。这种需求出现的原因是软件被分解为多个模块，每个模块都是完成某项功能的组件。

下面是一个 Maven 构建脚本的一个样例，名字是 pom.xml。注意，与 ant 的项目不同，Maven 默认去 src/main/java 找源码。
```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.bfh</groupId>
    <artifactId>myapp</artifactId>
    <packaging>jar</packaging>
    <version>1.0-SNAPSHOT</version>
    <name>myapp</name>
    <url>http://maven.apache.org</url>

    <!-- <dependencies>
    <dependency>
      <groupId>junit</groupId>
      <artifactId>junit</artifactId>
      <version>3.8.1</version>
      <scope>test</scope>
    </dependency>
  </dependencies> -->

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <configuration>
                    <source>1.8</source>
                    <target>1.8</target>
                </configuration>
            </plugin>
            <plugin>
                <!-- https://mvnrepository.com/artifact/org.apache.maven.plugins/maven-jar-plugin -->
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-jar-plugin</artifactId>
                <version>3.2.0</version>
                <configuration>
                    <archive>
                        <manifest>
                            <addClasspath>true</addClasspath>
                            <classpathPrefix>lib/</classpathPrefix>
                            <mainClass>com.bfh.Main</mainClass>
                        </manifest>
                    </archive>
                </configuration>
            </plugin>
        </plugins>
    </build>

</project>
```
执行 `mvn package` 后，生成如下文件：
```
.
├── pom.xml
├── src
│   └── main
│       └── java
│           └── com
│               └── bfh
│                   └── Main.java
└── target
    ├── classes
    │   └── com
    │       └── bfh
    │           └── Main.class
    ├── generated-sources
    │   └── annotations
    ├── maven-archiver
    │   └── pom.properties
    ├── maven-status
    │   └── maven-compiler-plugin
    │       └── compile
    │           └── default-compile
    │               ├── createdFiles.lst
    │               └── inputFiles.lst
    └── myapp-1.0-SNAPSHOT.jar
```
执行 `java -jar target/myapp-1.0-SNAPSHOT.jar` ，输出 `hello, maven.`。
