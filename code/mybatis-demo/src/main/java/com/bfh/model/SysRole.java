package com.bfh.model;

import lombok.Data;

import java.sql.Date;

@Data
public class SysRole {

    private Long id;

    private String roleName;

    private Integer enabled;

    private Long createBy;

    private Date createTime;
}
