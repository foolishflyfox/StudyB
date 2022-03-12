package com.bfh;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author benfeihu
 */
@Data
@AllArgsConstructor
public class StudentModel {
    private int id;
    private String name;
    private int age;
}
