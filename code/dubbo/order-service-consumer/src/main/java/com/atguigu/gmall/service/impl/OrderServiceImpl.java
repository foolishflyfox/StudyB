package com.atguigu.gmall.service.impl;

import com.atguigu.gmall.UserAddress;
import com.atguigu.gmall.service.OrderService;
import com.atguigu.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 1、将服务提供注册到注册中心
 *   1.1、导入 dubbo 依赖
 * 2、让服务消费者到注册中心订阅服务提供者的服务地址
 */
@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    UserService userService;
    @Override
    public void initOrder(String userId) {
        // 查询用户收货地址
        List<UserAddress> addressList = userService.getUserAddressList(userId);
        for (UserAddress address : addressList) {
            System.out.println(address);
        }
    }
}
