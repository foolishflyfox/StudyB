package com.bfh.jdbc;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class Employee {
    private Integer empId;
    private String empName;
    private Double salary;
}
