# mysql 事务

mysql 事务主要用于处理操作量大，复杂度高的数据。比如说，在人员管理系统中，你删除一个人员，你既需要删除人员的基本资料，也要删除和该人员相关的信息，如邮箱、文章等，这样，这些数据库操作语句就构成了一个事务。

在 mysql 中，只有使用了 innodb 数据库引擎的数据库或表才支持事务。事务处理可以用来维护数据库的完整性，保证成批的 SQL 语句要么全部执行，要么全部不执行。事务用来管理 insert、update、delete 语句。

一般来说，事务是必须满足4个条件(ACID)：原子性(Atomic)、一致性(Consistency)、隔离性(Isolation,又称为独立性)、持久性(Durability)。

- 原子性：一个事务中的所有操作，要么全部完成，要么全部不完成，不会结束在中间某个环节。事务在执行过程中发生错误，会被回滚(Rollback)到事务开始前的状态，就像这个事务从来没有备执行过。
- 一致性：在事务开始前和结束后，数据库的完整性没有被破坏。这表示写入的资料必须完全符合所有的预设规则，这包含资料的精确度、串联性以及后续数据库可以自发性地完成预定的工作。
- 隔离性：数据库允许多个并发事务同时对其数据库进行读写和修改的能力，隔离性可以防止多个事务并发执行时，由于交叉执行而导致数据的不一致。事务管理分为不同等级，包括读未提交、读提交、可重复读和串行化。
- 持久性：事务处理结束后，对数据的修改就是永久的，即便系统故障也不会丢失。

在 MySql 命令行的默认设置下，事务都是自动提交的，即执行 SQL 语句后就会马上执行 COMMIT 操作，因此要显式地开启一个事务须使用命令 BEGIN 或 START TRANSACTION，或者执行命令 SET AUTOCOMMIT=0，用来禁止使用当前会话的自动提交。

## 命令行事务测试

```sql
# 创建 db，需要指定 innodb 引擎
mysql> create table bfh_transaction_test( id int(5) ) engine=innodb;
Query OK, 0 rows affected, 1 warning (0.11 sec)

# 初始时刻没有数据
mysql> select * from bfh_transaction_test;
Empty set (0.01 sec)

# 开始事务
mysql> begin;
Query OK, 0 rows affected (0.00 sec)

# 插入一条记录
mysql> insert into bfh_transaction_test value(5);
Query OK, 1 row affected (0.01 sec)

# 在事务内可以看到新添加的数据，但如果新起一个客户端，将看不到该记录
mysql> select * from bfh_transaction_test;
+------+
| id   |
+------+
|    5 |
+------+
1 row in set (0.00 sec)

# 再添加一条记录
mysql> insert into bfh_transaction_test value(6);
Query OK, 1 row affected (0.00 sec)

# 仍然可以查到
mysql> select * from bfh_transaction_test;
+------+
| id   |
+------+
|    5 |
|    6 |
+------+
2 rows in set (0.00 sec)

# 提交事务，之后，其他客户端也能查到插入的两条记录
mysql> commit;
Query OK, 0 rows affected (0.00 sec)

# 重新开始一个新的事务
mysql> begin;
Query OK, 0 rows affected (0.00 sec)

# 插入一条记录
mysql> insert into bfh_transaction_test value(7);
Query OK, 1 row affected (0.00 sec)

mysql> select * from bfh_transaction_test;
+------+
| id   |
+------+
|    5 |
|    6 |
|    7 |
+------+
3 rows in set (0.00 sec)

# 事务回滚
mysql> rollback;
Query OK, 0 rows affected (0.00 sec)

# 插入操作没有生效
mysql> select * from bfh_transaction_test;
+------+
| id   |
+------+
|    5 |
|    6 |
+------+
2 rows in set (0.00 sec)
```

## mysql 事务隔离级别

查询事务隔离等级：
- mysql 8 及之后的版本：`show variables like 'transaction_isolation';`
- mysql 8 之前的版本：`select @@tx_isolation;`

mysql 支持的事务隔离等级：

- READ-UNCOMMITTED : 未提交读，A 事务已执行，但未提交；B 事务可以查询到 A 事务的更新后数据；在 A 事务回滚后，B 读到的数据就是脏数据；
- READ-COMMITTED : 已提交读，A 事务执行更新，B 事务查询，A 事务
- REPEATABLE-READ (mysql默认隔离级别) : 可重复读
- SERIALIZABLE : 串行化


