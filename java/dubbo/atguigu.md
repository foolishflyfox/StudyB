# dubbo 教程

## zookeeper 使用

从 https://zookeeper.apache.org/releases.html 下载指定的 zookeeper 版本，解压后的目录中有以下文件：
```shell
$ ls -F 
LICENSE.txt   NOTICE.txt   README.md   README_packaging.md   
bin/   conf/   docs/   lib/   logs/
```
其中 bin 下存放可执行文件。

修改 conf/zoo.cfg , 添加 `admin.serverPort=8888`。

通过 `cp conf/zoo_sample.cfg conf/zoo.cfg` 设置配置文件。

执行 `cd bin && ./zkServer.sh start` ，输出如下内容：
```txt
/Library/Java/JavaVirtualMachines/jdk1.8.0_271.jdk/Contents/Home/bin/java
ZooKeeper JMX enabled by default
Using config: /Users/fenghuabin/Code/Year2022/apache-zookeeper-3.7.0-bin/bin/../conf/zoo.cfg
Starting zookeeper ... STARTED
```
表示启动成功。

通过客户端测试，使用 `./zkCli.sh` 启动 zookeeper 客户端。之后开始测试：
```
[zk: localhost:2181(CONNECTED) 0] ls
ls [-s] [-w] [-R] path
[zk: localhost:2181(CONNECTED) 1] ls /
[zookeeper]
[zk: localhost:2181(CONNECTED) 2] create -e /bfh abc
Created /bfh
[zk: localhost:2181(CONNECTED) 3] ls /
[bfh, zookeeper]
[zk: localhost:2181(CONNECTED) 4] get /bfh
abc
```
表示 zookeeper 服务正常。

## 使用 dubbo 监控

下载 dubbo-admin 源码 : https://github.com/apache/dubbo-admin 。

进入 dubbo-admin-develop/dubbo-admin-server/src/main/resources ，可以修改 application.properties 指定 zookeeper 的服务地址。

在 dubbo-admin-develop 目录下执行 `mvn clean package`，生成 jar 文件 ./target/dubbo-admin-server-0.4.0.jar 。

通过 `mvn clean package -Dmaven.test.skip=true` 可跳过测试。

运行管理控制台：`java -jar dubbo-admin-server-0.4.0.jar`

在浏览器中输入 localhost:8080 ，输入用户名 root，密码 root 即可登录。





