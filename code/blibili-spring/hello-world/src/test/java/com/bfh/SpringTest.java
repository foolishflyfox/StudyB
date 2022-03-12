package com.bfh;

import com.bfh.servlet.BookServlet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author benfeihu
 * 使用 @ContextConfiguration(locations = "") 指定 spring 配置文件的位置
 * RunWith 指定用哪种驱动进行单元测试，默认就是 Junit
 */
@ContextConfiguration(locations = "classpath:ioc5.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class SpringTest {

    @Autowired
    BookServlet bookServlet;

    @Test
    public void test1() {
        System.out.println(bookServlet);
    }
}
