package com.atguigu.service;

import com.atguigu.gmall.UserAddress;

import java.util.List;

/**
 * 用户服务
 */
public interface UserService {
    /**
     * 按照用户 id 返回所有的收货地址
     * @param userId
     * @return
     */
    public List<UserAddress> getUserAddressList(String userId);
}
