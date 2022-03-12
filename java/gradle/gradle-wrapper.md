# Gradle Wrapper

参考：https://docs.gradle.org/current/userguide/gradle_wrapper.html

执行 Gradle 构建所推荐的方式是通过 Gradle Wrapper。Wrapper 是一个声明 gradle 版本的脚本，在需要时会自动下载指定版本的 gradle 文件。因此，开发者在获得一个 Gradle 项目后，不需要手动安装 gradle 的过程就可以开始工作。

Gradle Build 会先检查是否存在 gradle/wrapper/gradle-wrapper.properties 指定版本的 gradle，如果没有，就从指定服务器下载gradle安装包，将安装包保存并解压到用户目录，使用使用该版本的 gradle 进行 build。

简而言之，你可以收获以下好处：
- 可以为一个项目指定一个 gradle 版本，使构建过程保持兼容；
- 通过修改 Wrapper 的定义，可以方便的更换 Gradle 版本；

Q1: 如何指定下载地址，加速 gradle 的下载速度；
Q2: 下载到本地的路径在哪里定义；
Q3: 如何在 IDEA 的 command 使用；
Q4: IDEA 中所有关于 gradle 设置的含义；
Q5: gradle.build 的编写(gradle4);

对于用户而言，通常有3种不同的工作方式：
- 存在一个 gradle 项目，为其添加 Wrapper；
- 运行一个存在 Wrapper 的 gradle 项目；
- 对于一个存在 Wrapper 的 gradle 项目，升级其 gradle 版本；

下面将解释上述3种工作方式。

## 为一个 gradle 项目增加 Gradle Wrapper

如果希望创建 Wrapper 文件，需要在你的电脑上已经存在 gradle 可执行程序。执行 `gradle wrapper` 将在项目目录中创建必要的 Wrapper 文件。
```shell
$ gradle wrapper

BUILD SUCCESSFUL in 0s
1 actionable task: 1 executed
$ tree .
.
├── gradle
│   └── wrapper
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
├── gradlew
└── gradlew.bat

2 directories, 4 files

$ du -hd 0 gradle ; ls -lh gradlew gradlew.bat 
 60K	gradle
-rwxr-xr-x  1 huabinfeng  staff   5.2K 11 25 13:17 gradlew
-rw-r--r--  1 huabinfeng  staff   2.2K 11 25 13:17 gradlew.bat
```
初始化后的文件大约占 70k 大小(使用的 gradle 为 4.10)。

在 `gradle/gradle-wrapper.properties` 保持了 gradle 发行版相关的信息。包括：
- Gradle 发行版的下载服务器地址；
- Gradle 发行版的类型，默认是 `-bin` 类型的发行版，只包含可执行文件，没有示例代码和文档(即使已经安装的是`-all`发行版)；
- 用于执行构建的 gradle 版本号。默认 `wrapper` 任务使用与执行该任务所用的 gradle 相同的版本。

一个例子如下：
```
distributionBase=GRADLE_USER_HOME
distributionPath=wrapper/dists
distributionUrl=https\://services.gradle.org/distributions/gradle-4.10-bin.zip
zipStoreBase=GRADLE_USER_HOME
zipStorePath=wrapper/dists
```

通过在 `gradle wrapper` 后添加参数可以修改参数。
- `--gradle-version`: 设置指定版本；
- `--distribution-type`: 指定 gradle 类型，可以为 `all` 或 `bin`；
- `--gradle-distribution-url`: 指定 Gradle 发行版的 ZIP 文件地址。该参数将使上面两个参数失效，因为该参数已经带了这些信息。如果你希望使用公司内部的网络下载 gradle 发行版，该参数非常有用。

```shell
$ gradle wrapper --gradle-version 5.18 --distribution-type all

BUILD SUCCESSFUL in 0s
```
在生成的 gradle-wrapper.properties 中，有一行为 `distributionUrl=https\://services.gradle.org/distributions/gradle-5.18-all.zip` 就是按我们指定的参数生成的。

下面是一个项目的目录结构，我们对它进行分析。
```
.
├── a-subproject
│   └── build.gradle
├── gradle
│   └── wrapper
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
├── gradlew
├── gradlew.bat
└── settings.gradle
```
一个Gradle项目通常提供 `settings.gradle`，并且每个子项目都有一个 `build.gradle` 文件。wrapper 文件夹在 gradle 文件夹中，其中的文件有：
- `gradle-wrapper.jar`: 用于下载 Gradle 发行版；
- `gradle-wrapper.properties`: 配置 wrapper；
- `gradlew`/`gradlew.bat`: 用于执行 build 命令的 shell 脚本/windows批处理脚本。

## 使用 Gradle Wrapper

建议始终使用 Wrapper 执行 build，保障可靠性、可控性和标准化执行。通过 Wrapper 执行看起来像通过安装的 Gradle 命令执行命令一样。

如果当前路径为子项目文件夹，在执行 Wrapper 时，你需要指定正确的路径，如 `../../gradlew tasks`。


##  使用 Gradle Wrapper

## gradle 的配置

### gradle home 的配置

### 配置文件

- `init.gradle`: 执行 gradle 前会被运行的命令；

## gradle 命令

- `gradle tasks`: 打印当前 gradle 支持的命令；
- `gradle wrapper`: 创建 gradle wrapper 文件；
