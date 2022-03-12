
## MyBatis 从入门到精通

### 第一章

使用的数据库 bfh_test。
使用的表：`create table student(id int primary key auto_increment,name varchar(20), age int);`

### 第二章

创建表：
```
create table sys_user (id bigint not null auto_increment comment '用户ID',
    user_name       varchar(50) comment '用户名',
    user_password   varchar(50) comment '密码',
    user_email      varchar(50) comment '邮箱',
    user_info       text        comment '简介',
    head_img        blob        comment '头像',
    create_time     datetime    comment '创建时间',
    primary key(id) 
);
alter table sys_user comment '用户表';

create table sys_role ( id bigint not null auto_increment comment '角色ID',
    role_name       varchar(50) comment '角色名',
    enabled         int         comment '有效标志',
    create_by       bigint      comment '创建人',
    create_time     datetime    comment '创建时间',
    primary key(id)
);
alter table sys_role comment '角色表';

create table sys_privilege ( id bigint not null auto_increment comment '权限ID',
    privilege_name  varchar(50)     comment '权限名称',
    privilege_url   varchar(200)    comment '权限URL',
    primary key (id)
);
alter table sys_privilege comment '权限表';

create table sys_user_role (
    user_id     bigint  comment '用户ID',
    role_id     bigint  comment '角色ID'
);
alter table sys_user_role comment '用户角色关联表';

create table sys_role_privilege (
    role_id         bigint  comment '角色ID',
    privilege_id    bigint  comment '权限ID'
);
alter table sys_role_privilege comment '角色权限关联表';

# 为了方便对表直接进行操作，此处没有创建表之间的外键关系，对于表之间的关系，会通过业务逻辑来进行限制
# 为方便对后面的测试，先在表中插入一些测试数据
insert into sys_user values ('1', 'admin', '123456', 'admin@mybatis.xx',
    '管理员',null,'2022-01-05 00:27:56');
insert into sys_user values ('1001', 'test', '123456', 'test@mybatis.xx',
    '测试用户',null,'2022-01-05 00:28:51');

insert into sys_role values ('1', '管理员', '1', '1', '2022-01-05 00:30:14');
insert into sys_role values ('2', '普通用户', '1', '1', '2022-01-05 00:30:50');

insert into sys_user_role values ('1', '1');
insert into sys_user_role values ('1', '2');
insert into sys_user_role values ('1001', '2');

insert into sys_privilege values ('1', '用户管理', '/users');
insert into sys_privilege values ('2', '角色管理', '/roles');
insert into sys_privilege values ('3', '系统日志', '/logs');
insert into sys_privilege values ('4', '人员维护', '/persons');
insert into sys_privilege values ('5', '单元维护', '/companies');

insert into sys_role_privilege value ('1', '1');
insert into sys_role_privilege value ('1', '3');
insert into sys_role_privilege value ('1', '2');
insert into sys_role_privilege value ('2', '4');
insert into sys_role_privilege value ('2', '5');

```

在 Java 中的基本类型会有默认值，例如当某个类中存在 `private int age` 字段时，创建这个类时，age会有默认值0。
当使用age属性时，它总是会有值，因此，在某些情况下无法实现使 age 为 null。
并且在动态 SQL 的部分，如果使用 age != null 进行判断，结果总是为 true，因此会导致很多隐藏的问题。

所以，在实体类中不要使用基本类型。

创建实体类的过程比较枯燥，后面可以通过 MyBatis 官方提供的工具 MyBatis Generator(MyBatis 代码生成器，简称 MBG)根据数据库中的信息自动生成这些类，以减少工作量。

#### 使用 XML 方式

MyBatis 3.0 相比于 2.0 最大的变化是，支持是用哪个接口来调用方法。

以前使用 SqlSession 通过命名空间调用 MyBatis 方法时，首先需要用到命名空间和方法id组成的字符串来调用相应的方法。
当参数多于1个时，需要将所有的参数放到一个 Map 对象中。通过 Map 传递多个参数，使用起来很不方便，而且还无法避免很多重复的代码。

使用接口调用就会方便很多，MyBatis 使用 Java 的动态代理可以直接通过接口来调用相应的方法，不需要提供接口的实现类，更不需要在实现类中使用 SqlSession 以通过命名空间间接调用。

另外，当有多个参数时，通过参数注解 `@Param` 设置参数的名字省去了手动构造 Map 参数的过程，尤其在 Spring 中使用时，可以配置为自动扫描所有的接口类，注解将接口注入到需要用到的地方。

在 src/main/resources 的 com.bfh.mapper 目录下创建 5 个表各自对应的 XML：UserMapper.xml、RoleMapper.xml、PrivilegeMapper.xml、UserRoleMapper.xml 和 RolePrivilegeMapper.xml。
然后在 src/main/java 下面建包 com.bfh.mapper 创建包 com.bfh.mapper。
接着，在该包下创建 XML 文件对应的接口类，分别为 UserMapper.java、RoleMapper.java、PrivilegeMapper.java、UserRoleMapper.java 和 RolePrivilegeMapper.java。

映射 XML 和接口的命名需要符合如下规则：

- 当只使用 XML 而不使用接口时，namespace 值可以设置为任意不重复名称；
- 标签的 id 属性值在任何时候都不能出现英文句号，并且同一命名空间不能实现重复 id；
- 因为接口方法时可重载的，所以接口中可以出现多个同名但参数不同的方法，但是 XML 中 id 的值不能重复，因而接口中所有同名方法回对应 XML 中同一个 id 的方法。最常见的用法是，同名方法中其中一个方法增加一个 RowBound 类型的参数用于实现分页查询；

XML 中一些标签和属性：

- `<select>`：映射查询语句使用的标签；
- `id`：命名空间中的唯一标识符，可用来表示这条语句；
- `resultMap`：用于设置返回值的类型和映射关系；
- `select * from sys_user where id = #{id}`：查询语句；

resultMap 是一种很重要的配置结果映射的方法，包含如下属性：

- `id`：必填，唯一；
- `type`: 必填，用于配置查询所映射到的 Java 对象类型；
- `extends`：选填，可以配置当前的 resultMap 继承自其他的 resultMap，属性值为继承 resultMap 的 id；
- `autoMapping`: 选填，true 或 false，用于配置是否启用非映射字段(没有在 resultMap 中配置的字段)的自动映射功能，该配置可以覆盖全局的 autoMappingBehavior；

以上是 resultMap 的属性，resultMap 包含的所有标签如下：
- constructor：配置使用构造方法正如结果，包含以下两个子标签。
    - idArg：id 参数，标记结果作为 id，可以提高整体性能；
    - arg：注入到构造函数的一个普通结果；
- id：一个 id 接口，标记结果作为 id(唯一值)，可提高整体性能；
- result：注入到 Java 对象属性的普通结果；
- association: 一个复杂的类型关联，许多结果将包成这种类型；
- collection：复杂类型的集合；
- discriminator：根据结果值决定使用哪个结果映射；
- case：基于某些值的结果映射；

id 和 result 标签包含的属性：

- property：映射到的列结果的属性；
- column：从数据库中得到的列名(或是列的别名)；
- javaType：一个 java 类的完全限定名，或一个类型别名。
- jdbcType：列对应的数据库类型。JDBC 类型仅仅需要对插入、更新、删除操作可能为空的列就行处理。这里是 JDBC jdbcType 的需要，而不是 MyBatis 需要；
- typeHandler：使用这个属性可以覆盖默认的类型处理器
 
 可以通过在 resultMap 中配置 property 属性和 column 属性的映射，或者在 SQL 中设置别名这两种方式实现将查询列映射到对象属性的目的。
 
### MyBatis 动态 SQL

MyBatis 的强大特性之一便是它的动态 SQL。根据不同条件拼接 SQL 语句时，不仅不能忘了必要的空格，还要注意省略掉列名列表最后的逗号，处理方式麻烦且凌乱。MyBatis 的动态 SQL 能简化过程。

MyBatis 3 采用了功能强大的 OGNL(Object-Graph Navigation Language) 表达式语言消除了许多其他标签，以前是 MyBatis 的动态 SQL 在 XML 中支持的集中供暖标签：

- if
- choose (when、otherwise)
- trim (where、set)
- foreach
- bind

#### if 用法

if 标签通常用于 WHERE 语句中，通过判断参数值来决定是否使用某个查询条件，他也经常用于 UPDATE 语句中判断是否更新某个字段，还可以在 INSERT 语句中用于判断是否插入某个字段的值。

**在 WHERE 条件中使用 if**

需求：当只输入用户名时，根据用户名就行模糊查询；只输入邮箱时，根据邮箱进行完全匹配；同时输入用户名和邮箱时，用这两个条件去查询匹配的用户；

#### chose 用法

if 标签提供来基本的条件判断，但是它无法实现 if...else、if...else... 的逻辑，要想实现这种逻辑，就需要用到 choose when otherwise 标签。choose 元素中包含 when 和 otherwise 两个标签。
一个 choose 中至少有一个 when，有 0 个或 1 个 otherwise。

需求：当参数 id 有值的时候，优先使用 id 查询，当 id 没有值时，就去判断用户名是否有值，如果有值就用用户名查询，如果用户名也没有值，SQL 查询无结果。

#### where、set、trim 用法

where 标签的作用：如果该标签包含的元素中有返回值，就插入一个 where；如果 where 后面的字符以 and 和 or 开头，就将它们剔除。

set 标签的作用：如果该标签包含的元素中有返回值，就插入一个 set；如果 set 后面的字符串是以逗号结尾，就将这个逗号剔除。

where 和 set 标签的功能都可以用 trim 标签来实现，并且在底层就是通过 TrimSqlNode 实现的。

where 标签对应的 trim 实现如下：
```xml
<trim prefix="WHERE" prefixOverrides="AND | OR ">
...
</trim>
```
AND 和 OR 后面的空格不能省略。

set 标签对应的 trim 实现为：
```xml
<trim prefix="SET" suffixOverrides=",">
...
</trim>
```
trim 标签有如下属性：
- prefix：当 trim 元素内包含内容时，会给内容增加 prefix 指定的前缀；
- prefixOverrides：当 trim 元素内包含内容时，会将内容中匹配的前缀字符串去掉；
- suffix：当 trim 元素内包含内容时，会给内容增加 suffix 指定的后缀；
- suffixOverrides：当 trim 元素包含内容时，会将内容匹配的后缀字符串去掉；

#### foreach 实现 in 集合

foreach 实现 in 集合(或数组)是最简单和常用的一种情况。下面介绍如何根据插入的用户 id 集合查询出所有符合条件的用户。在 UserMapper 接口中添加如下方法：
```java
List<SysUser> selectByIdList(List<Long> idList);
```
UserMapper.xml 中添加：
```xml
    <select id="selectByIdList" resultType="com.bfh.model.SysUser">
        select id,
            user_name userName,
            user_password userPassword,
            user_email userEmail,
            user_info userInfo,
            head_img headImg,
            create_time createTime
        from sys_user
        where id in
        <foreach collection="list" open="(" close=")" separator="," item="id" index="i">
            #{id}
        </foreach>
    </select>
```
foreach 包含以下属性：
- collection：必填，值为要迭代循环的属性名。
- item：变量名，值为从迭代对象中取出的每一个值。
- index：索引的属性名，在集合数组情况下值为当前索引值，当迭代循环对象是 Map 类型时，值为 Map 的 key。
- open：整个循环内容开头的字符串。
- close：整个循环内容结束的字符串。
- separator：每次循环的分隔符。

collection 的属性设置：

1. 只有一个数组参数或集合参数。
    a. 当参数类型为集合的时候，默认会转换为 Map 类型，并添加一个 key 为 collection 的值；
    b. 如果参数类型为 List 集合，那么就继续添加一个 key 为 list 的值。这样，当 collection="list" 时，就能得到这个集合；
    c. 使用数组参数时，就需要把 foreach 标签中的 collection 属性设置为 array；
    
上面提到的是数组或集合类型的参数默认的名字。推荐使用 @Param 指定参数的名称。这时，collection 就设置为通过 @Param 注解指定的名字。

**foreach 实现批量插入**

如果数据库支持批量插入，就可以通过 foreach 来实现。批量插入时 SQL-92 的新增特性，目前支持的数据库有 DB2、SQL Server 2008 及以上、PostgreSQL 8.2 及以上、MySQL、SQLite 3.7.11 及以上、H2。

批量插入的语法为：
```sql
insert into table (column-a, [column-b, ...])
values  ('value-1a', ['value-1b', ...]),
        ('value-2a', ['value-2b', ...]),
    ... ...
```

**foreach 实现动态 UPDATE**

这一节主要介绍当参数类型时 Map 时，foreach 如何实现动态 UPDATE。

当参数是 Map 类型的时候，foreach 标签的 index 属性值对应的不是索引值，而是 Map 中的 key，利用这个 key 可以实现动态 UPDATE。

### mysql when then case

case when 语句用于计算条件列表并返回多个可能结果表达式之一。

CASE 具有两种格式：简单 CASE 函数将某个表达式与一组简单表达式进行比较以确定结果。CASE 搜索函数计算一组布尔表达式以确定结果。两种格式都支持可选的 ELSE 参数。

- 简单 CASE 函数
```sql
CASE <单值表达式>
    WHEN <表达式值> THEN <SQL语句或返回值>
    WHEN <表达式值> THEN <SQL语句或返回值>
    ... ...
    WHEN <表达式值> THEN <SQL语句或返回值>
    ELSE <SQL语句或表达式>
END
```
用法1：
```sql
select *, (case sex when 1 then '男' when 0 then '女' else '保密' end) as sex_text from student;
```
用法2：
```sql
select *, (case when sex=1 then '男' when sex=0 then '女' else '保密' end) as sex_text from student;
```

案例：批量修改表数据。
表创建：
```sql
create table course(course_id int primary key, course_name varchar(128), sort smallint);
insert into course values(1, "课程A", 1);
insert into course values(2, "课程B", 2);
insert into course values(3, "课程C", 3);
```
查询的结果为：
```text
+-----------+-------------+------+
| course_id | course_name | sort |
+-----------+-------------+------+
|         1 | 课程A       |    1 |
|         2 | 课程B       |    2 |
|         3 | 课程C       |    3 |
+-----------+-------------+------+
```
通过 case 排序：
```sql
update course set sort = (
    case    when course_id=1 then 3
            when course_id=2 then 1
            when course_id=3 then 2
    end 
);
```
执行后，course 表的结果为：
```text
+-----------+-------------+------+
| course_id | course_name | sort |
+-----------+-------------+------+
|         1 | 课程A       |    3 |
|         2 | 课程B       |    1 |
|         3 | 课程C       |    2 |
+-----------+-------------+------+
```
注意，在没有 where 字段时，会对全表扫描，因此，对于需要特定 id 进行处理时，最好在 where 后限定特定的 ID。

全部的 sort 都加1：
```sql
update course set sort = sort+1;
```
执行后，course 表的结果为：
```text
+-----------+-------------+------+
| course_id | course_name | sort |
+-----------+-------------+------+
|         1 | 课程A       |    4 |
|         2 | 课程B       |    2 |
|         3 | 课程C       |    3 |
+-----------+-------------+------+
```
部分的 sort 加1：
```sql
update course set sort = sort+1 where course_id in (1,3);
```
执行后的结果为：
```text
+-----------+-------------+------+
| course_id | course_name | sort |
+-----------+-------------+------+
|         1 | 课程A       |    5 |
|         2 | 课程B       |    2 |
|         3 | 课程C       |    4 |
+-----------+-------------+------+
```
两个条件同时选：
```sql
update course set sort = sort+1 where ((course_id=1 and course_name='课程A') or (course_id=2 and course_name='课程B'));
```
结果为：
```text
+-----------+-------------+------+
| course_id | course_name | sort |
+-----------+-------------+------+
|         1 | 课程A       |    6 |
|         2 | 课程B       |    3 |
|         3 | 课程C       |    4 |
+-----------+-------------+------+
```

## Mybatis 代码生成器

当数据库表的字段比较少的时候，自己写 Mapper.xml 和接口还可以接受。一旦字段过多，或者需要在很多个表中写这些基本方法时，就会很麻烦，不仅代码量大，而且字段过多很容易出错。

MyBatis Generator 提供了一个代码生成器。MBG 通过丰富的配置可以生成不同类型的代码，代码包含了数据库表对应的实体类、Mapper 接口类、MapperXML 文件和 Example 对象等，这些代码文件几乎包含了全部的单表操作。

MBG 版本和 MyBatis 的版本没有直接关系。MBG 可以生成 MyBatis 和 iBATIS 的代码。



