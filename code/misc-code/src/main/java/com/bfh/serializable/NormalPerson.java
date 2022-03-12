package com.bfh.serializable;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author benfeihu
 */
@Data
@AllArgsConstructor
public class NormalPerson {

    private String name;

    private Integer age;
}
