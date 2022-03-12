
数据库环境：
```sql
/*
SQLyog Ultimate v9.20 
MySQL - 5.1.37-community : Database - tx
*********************************************************************
*/


/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`tx` /*!40100 DEFAULT CHARACTER SET gb2312 */;

USE `tx`;

/*Table structure for table `account` */

DROP TABLE IF EXISTS `account`;

CREATE TABLE `account` (
  `username` varchar(50) NOT NULL,
  `balance` int(11) DEFAULT NULL,
  PRIMARY KEY (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=gb2312;

/*Data for the table `account` */

insert  into `account`(`username`,`balance`) values ('Jerry',800),('Tom',100000);

/*Table structure for table `book` */

DROP TABLE IF EXISTS `book`;

CREATE TABLE `book` (
  `isbn` varchar(50) NOT NULL,
  `book_name` varchar(100) DEFAULT NULL,
  `price` int(11) DEFAULT NULL,
  PRIMARY KEY (`isbn`)
) ENGINE=InnoDB DEFAULT CHARSET=gb2312;

/*Data for the table `book` */

insert  into `book`(`isbn`,`book_name`,`price`) values ('ISBN-001','book01',100),('ISBN-002','book02',200),('ISBN-003','book03',300),('ISBN-004','book04',400),('ISBN-005','book05',500);

/*Table structure for table `book_stock` */

DROP TABLE IF EXISTS `book_stock`;

CREATE TABLE `book_stock` (
  `isbn` varchar(50) NOT NULL,
  `stock` int(11) DEFAULT NULL,
  PRIMARY KEY (`isbn`)
) ENGINE=InnoDB DEFAULT CHARSET=gb2312;

/*Data for the table `book_stock` */

insert  into `book_stock`(`isbn`,`stock`) values ('ISBN-001',1000),('ISBN-002',2000),('ISBN-003',3000),('ISBN-004',4000),('ISBN-005',5000);

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
```

```
update account set balance=10000; update book set price=100; update book_stock set stock = 1000;
```

声明式事务：以前通过复杂的编程来编写一个事务，替换为只需要告诉 Spring 哪个方法是事务方法即可；最终效果：
```java
BookService {

    @this is a tx-method (Transactional)
    public void checkout() {
        // xxx
    }
}
```

编程式事务：
```java
try {
    // 获取链接
    // 设置非自动提交
    chain.doFilter();
    // 提交
} catch (Exception e) {
    // 回滚
} finally {
    // 关闭连接释放资源
}
```

事务切面称为事务管理器。

快速为某个方法添加事务：

1. 我们目前都使用 DatasourceTransactionManager，配置出这个事务管理的组件；