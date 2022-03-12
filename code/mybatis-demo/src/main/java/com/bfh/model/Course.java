package com.bfh.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author benfeihu
 */
@Data
@NoArgsConstructor
public class Course {
    private Integer courseId;

    private String courseName;

    private Integer sort;

    private Integer monitor;

    private Integer classRoom;
}
