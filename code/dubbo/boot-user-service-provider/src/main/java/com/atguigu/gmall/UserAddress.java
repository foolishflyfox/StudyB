package com.atguigu.gmall;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserAddress implements Serializable {
    private Integer id;
    private String address;
    private String userId;
    private String consignee;
    private String phoneNum;
    private Boolean isDefault;
}
