package com.bfh.mapper;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author benfeihu
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class A implements Serializable {
    private Long a;
    private String b;
    private Integer c;
}
