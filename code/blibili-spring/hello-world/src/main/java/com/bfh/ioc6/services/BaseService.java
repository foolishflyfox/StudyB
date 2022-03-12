package com.bfh.ioc6.services;

import com.bfh.ioc6.dao.BaseDao;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author benfeihu
 */
public class BaseService<T> {
    @Autowired
    BaseDao<T> baseDao;

    public void save() {
        System.out.println("自动注入的 dao. " + baseDao);
        baseDao.save();
    }
}
