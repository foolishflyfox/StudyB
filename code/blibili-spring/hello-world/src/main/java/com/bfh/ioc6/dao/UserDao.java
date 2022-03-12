package com.bfh.ioc6.dao;

import com.bfh.ioc6.bean.User;
import org.springframework.stereotype.Repository;

/**
 * @author benfeihu
 */
@Repository
public class UserDao extends BaseDao<User> {
    @Override
    public void save() {
        System.out.println("保存用户");
    }
}
