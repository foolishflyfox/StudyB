package com.bfh.jdbc;

import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public class EmployeeDao {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public int saveEmployee(Employee employee) {
        String sql = "insert into employee(emp_name,salary) values(?, ?)";
        // 连接与断开 db 由 jdbcTemplate 管理
        int update = jdbcTemplate.update(sql, employee.getEmpName(), employee.getSalary());
        return update;
    }

}
