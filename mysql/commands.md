# MySQL 数据库命令

- `show databases`
```
+--------------------+
| Database           |
+--------------------+
| information_schema |
| mysql              |
| performance_schema |
| sys                |
+--------------------+
```
- *information_schema*: 保存着关于mysql服务器所维护的所有其他数据库的信息，如数据库名，数据库的表，表栏的数据类型与访问权限等，也就是说当你建立一个新的数据库，或者在已有的数据库中增删改表的话，都会记录在information_schema库中
- *mysql*: 存储数据库的用户、权限设置、关键字等mysql自己需要使用的控制和管理信息
- *performance_schema*: 主要用于收集数据库服务器性能参数
- *sys*:

## DDL

- 建数据库：`create database 数据库名;`
- 建表：`create table (id int, name varchar(15))`

## 查看
- 查看创建表的语句：`show create table 表名;`
- 查看建库的语句：`show create database 库名;`