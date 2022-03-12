package com.bfh.model;

import lombok.*;

import java.sql.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SysUser {

    private Long id;

    private String userName;

    private String userPassword;

    private String userEmail;

    private String userInfo;

    private byte[] headImg;

    private Date createTime;

}
