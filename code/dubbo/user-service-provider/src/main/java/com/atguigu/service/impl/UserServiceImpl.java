package com.atguigu.service.impl;

import com.atguigu.gmall.UserAddress;
import com.atguigu.service.UserService;

import java.util.Arrays;
import java.util.List;

public class UserServiceImpl implements UserService {
    @Override
    public List<UserAddress> getUserAddressList(String userId) {
        UserAddress address1 = UserAddress.builder().id(1)
                .address("上海市长宁区xxx小区")
                .userId("0001")
                .consignee("张三")
                .phoneNum("123456")
                .isDefault(true)
                .build();
        UserAddress address2 = UserAddress.builder().id(2)
                .address("杭州市西湖区xxx小区")
                .userId("0002")
                .consignee("李四")
                .phoneNum("111111")
                .isDefault(false)
                .build();
        return Arrays.asList(address1, address2);
    }
}
