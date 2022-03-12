# redis

## redis 的安装

下载 redis 压缩包，redis-x.x.x.tar.gz，解压后，进入解压后的文件，执行 make install 就完成了 redis 的安装。

## 启动 redis

启动服务器 `redis-server`，启动客户端 `redis-client`。

基本操作：set/get。
```
127.0.0.1:6379> set name foolishflyfox
OK
127.0.0.1:6379> get name
"foolishflyfox"
127.0.0.1:6379> get nm
(nil)
```

## 操作

- `select index`: 选择指定的库；