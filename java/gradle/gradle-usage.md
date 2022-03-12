# gradle

- group：通常用于指定项目所归属的组织，通常分为两段，第一段为域，如 org、com、cn 等；第二段为公司名。

## hello world

最简单的 hello, world ：
```gradle
task hello {
    doLast {
        println 'hello,world'
    }
}
```
执行 `gradle -q hello` 输出为：hello,world  。

## ext

变量定义，例如:
```gradle
ext {
    apple='apple inc'
    cp = System.getProperty("java.class.path")
}

task hello {
    doLast {
        println "$apple"
        println "cp = ${cp}!"
    }
}
```
输出为：
```
apple inc
cp = /Users/huabinfeng/.gradle/wrapper/dists/gradle-4.10-all/54ye4nru8k2rpf7xwj15he4fo/gradle-4.10/lib/gradle-launcher-4.10.jar!
```
