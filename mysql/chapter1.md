# 数据库的概述

## 为什么要使用数据库

- 持久化(Persistence): 把数据保存到可掉电式存储设备中以供之后使用。大多数情况下，特别是企业级应用，数据持久化意味着将内存中的数据保存到硬盘上加以固化，而持久化的实现过程大多通过各种关系数据库来完成。
- 持久化的重要作用是将内存中的数据存储在关系型数据库中，当然也可以存储在磁盘文件、XML 数据文件中。

## 数据库与数据管理系统

### 数据库的相关概念

DB：数据库，Database，即存储数据的仓库，其本质是一个文件系统。在 mac 中，数据库存放在 /usr/local/mysql-xxx 中。
DBMS：数据库管理系统，是一种操作和管理数据库的大型软件，用于建立、使用和维护数据库，对数据库进行统一管理和控制。用户通过数据库管理系统访问数据库中表内的数据。
SQL：结构化查询语句（Structure Query Language），专门用来与数据库通信的语言。

### 数据库与数据库管理系统的关系

数据库管理系统可以管理多个数据库，一般开发人员会针对每个应用创建一个数据库，为保存应用中实体的数据，一般会在数据库中创建多个表，以保存程序中实体用户的数据。

### 常见的数据库管理系统排名 DBMS

目前互联网上常见的DBMS有 Oracle、MySQL、SQL Server、DB2、PostgreSQL、Access。

MySQL 是一个开放源代码的关系型数据库管理系统。

## RDBMS 与非 RDBMS

### 关系型数据库

这种类型的数据库是最古老的数据库类型，关系型数据库模型是把复杂的数据结构归结为简单的二元关系，即二维表格形式。

关系型数据库以行(row)和列(column)的形式储存数据，以便于用户理解。这一系列的行和列被称为表 table，一组表组成了一个库。


关系型数据库的典型数据结构就是数据表，这些数据表的组成都是结构化的(Structured)。

E-R (entity-relation, 实体-联系)模型中主要概念是实体集、属性、联系集。

一个实体集 class 对应于数据库中的一个表，一个实体对应于数据表中的一行(row)，也称为一条记录(record)。一个属性(attribute)对应于数据库表中的一列(column)，也称为一个字段。

ORM（Object Relational Mapping）：数据库中的一个表 <---> Java 或 Python 中的一个类 

