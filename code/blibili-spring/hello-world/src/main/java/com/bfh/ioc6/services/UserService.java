package com.bfh.ioc6.services;

import com.bfh.ioc6.bean.User;
import com.bfh.ioc6.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author benfeihu
 */
@Service
public class UserService extends BaseService<User> {

//    @Autowired
//    UserDao userDao;
//
//    public void save() {
//        userDao.save();
//    }

}
