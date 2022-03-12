package com.pdai.tech.utest;

/**
 * @author benfeihu
 */
public class DemoService {
    private DemoDao demoDao;

    public DemoService(DemoDao demoDao) {
        this.demoDao = demoDao;
    }

    public int getDemoStatus() {
        return demoDao.getDemoStatus();
    }
}
