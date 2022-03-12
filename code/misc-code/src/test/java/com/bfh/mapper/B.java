package com.bfh.mapper;

import lombok.Data;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author benfeihu
 */
@ToString
@Setter
public class B implements Serializable {
    private String a;
    private Long b;
    private Integer d;
}
