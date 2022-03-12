package com.bfh.jdbc;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class TxTest {

    ApplicationContext ioc = new ClassPathXmlApplicationContext("jdbc-ioc.xml");
    JdbcTemplate jdbcTemplate = ioc.getBean(JdbcTemplate.class);
    NamedParameterJdbcTemplate namedParameterJdbcTemplate = ioc.getBean(NamedParameterJdbcTemplate.class);
    @Test
    public void test() throws SQLException {
        DataSource bean = ioc.getBean(DataSource.class);
        Connection connection = bean.getConnection();
        System.out.println(connection);
        connection.close();
    }

    @Test
    public void test01() {
        System.out.println(jdbcTemplate);
    }

    // 实验2：更新一个员工数据
    @Test
    public void test02() {
        String sql = "update employee set salary=? where emp_id=?";
        int update = jdbcTemplate.update(sql, 1301.0, 5);
        System.out.println("update " + update + " rows");
    }

    // 实验3：批量插入
    // 插入：insert into employee(emp_name, salary) values(?,?)
    @Test
    public void test03() {
        String sql = "insert into employee(emp_name, salary) values(?,?)";
        // List 长度就是 sql 要执行多少遍
        // Object[] 是每次执行要用的参数
        List<Object[]> batchArgs = Lists.newArrayList();
        batchArgs.add(new Object[]{"张三", 998.98});
        batchArgs.add(new Object[]{"李四", 2998.98});
        batchArgs.add(new Object[]{"王五", 3998.98});
        batchArgs.add(new Object[]{"赵六", 4998.98});
        int[] ints = jdbcTemplate.batchUpdate(sql, batchArgs);
        System.out.println(Arrays.toString(ints));
    }
    /**
     * 实验4：查询 emp_id = 5 的数据记录，封装为一个 Java 对象返回
     *       javaBean 想要和数据库中字段名一致，否则无法完成封装
     * select emp_id empId, emp_name empName, salary from employee where emp_id=?
     * jdbcTemplate 在方法级别进行了区分
     * 查询集合：jdbcTemplate.query  如果查询没结果就报错
     * 查询当个对象：jdbcTemplate.queryForObject
     *
     */
    @Test
    public void test04() {
        String sql = "select emp_id empId, emp_name empName, salary from employee where emp_id=?";
        Employee employee = jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(Employee.class), 7);
        System.out.println(employee);
    }

    /*
     * 实验5：查询 salary > 4000 的数据记录，封装为 List 集合返回
     */
    @Test
    public void test05() {
        String sql = "select emp_id empId, emp_name empName, salary from employee where salary > ?";
        List<Employee> query = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Employee.class), 4000);
        for (Employee employee : query) {
            System.out.println(employee);
        }
    }

    /**
     * 实验6：查询最大薪资
     */
    @Test
    public void test06() {
        String sql = "select max(salary) from employee";
//        Double query = jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(Double.class));  // 报错
        Double query = jdbcTemplate.queryForObject(sql, Double.class);
        System.out.println(query);
    }

    /**
     * 实验7：使用带有具名参数的 SQL 语句插入一条员工记录，并以 Map 形式传入参数
     * 具名参数：
     * 占位符参数：? 的顺序千万不能乱，传承的时候一定要注意
     */
    @Test
    public void test07() {
        String sql = "insert into employee(emp_name,salary) values(:empName, :salary)";
        Map<String, Object> paramMap = Maps.newHashMap();
        paramMap.put("empName", "田七");
        paramMap.put("salary", 9987.99);
        int update = namedParameterJdbcTemplate.update(sql, paramMap);
        System.out.println("update " + update + " rows");
    }

    /**
     * 实验8：重复实验7，以 SqlParameterSource 形式传入参数值
     */
    @Test
    public void test08() {
        String sql = "insert into employee(emp_name,salary) values(:empName, :salary)";
        Employee employee = new Employee();
        employee.setEmpName("哈哈");
        employee.setSalary(10099.88);
        int update = namedParameterJdbcTemplate.update(sql, new BeanPropertySqlParameterSource(employee));
        System.out.println("update " + update + " rows");
    }

    /**
     * 实验9：
     */
    @Test
    public void test09() {
        EmployeeDao employeeDao = ioc.getBean(EmployeeDao.class);
        Employee employee = new Employee();
        employee.setEmpName("嘿嘿");
        employee.setSalary(10001.0);
        int update = employeeDao.saveEmployee(employee);
        System.out.println("update " + update + " row");
    }

}
