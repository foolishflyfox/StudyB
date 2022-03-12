
## 子项目的结构

子项目的文件目录结构通常为：
```text
src
├── main
│   ├── java
│   └── resources
└── test
    ├── java
    └── resources
```
本项目中，可以通过命令 `ls -d my* | xargs -I rdir mkdir -p rdir/src/{main/{java,resources},test/{java,resources}}` 初始化子目录。 

## buildscript

在编写 build.gradle 时，在 build.gradle 文件中经常看到这样的代码：
```groovy
buildscript {
    repositories {
        mavenCentral()
    }
}
repositories {
    mavenCentral()
}
```
为什么 repositories 要声明两次呢？buildscript 代码块的声明与下半部分的声明有什么不同？

buildscript 中的声明式 gradle 脚本自身需要使用的资源。可以声明的资源包括依赖项、第三方插件、maven 仓库地址等。
而在 build.gradle 文件中直接声明的依赖项、仓库地址等信息是项目自身需要的资源。

gradle 是由 groovy 语言编写的，支持 groovy 语法，可以灵活地使用已有的各种 ant 插件、基于 jvm 的类库，这也是它比 maven、ant 等构建脚本强大的原因。
虽然 gradle 支持开箱即用，但是如果你想在脚本中使用一些第三方的插件、类库等，就需要自己动手添加这些插件、类库的应用。
而这些插件、类库又不是直接服务于项目的，而是支持其他 build 脚本的运行。所以你应当将这部分的引用放置在 buildscript 代码块中。
gradle 在执行脚本时，会优先执行 buildscript 代码块中的内容，然后才会执行剩余的 build 脚本。

在 buildscript 代码块中，你可以对 dependencies 使用 classpath 声明。
该 classpath 声明说明了在执行其余的 build 脚本时，class loader 可以使用这些你提供的依赖项，这也正是我们使用 buildscript 代码块的目的。
例如下面的代码：
```groovy
buildscript {
    ext {
        commonsCodecVersion = '1.6'
    }
    repositories {
        maven { url "http://maven.aliyun.com/nexus/content/groups/public/" }
        mavenCentral()
    }
    dependencies {
        // 如果要使用全局变量，必须使用双引号，不能使用单引号
        classpath "commons-codec:commons-codec:$commonsCodecVersion"
    }

}
// 执行该任务: gradle mytask
task mytask {
    println org.apache.commons.codec.digest.DigestUtils.md5Hex("abcdefg")
    println DigestUtils.md5Hex("abcdefg")
}
```
通过执行 `gradle mytask`，输出如下：`7ac66c0f148de9519b8bd264312c4d64`，该值就是对 `abcdefg` 的 md5 hash 值。


