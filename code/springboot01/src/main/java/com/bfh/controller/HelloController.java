package com.bfh.controller;

import com.bfh.bean.Car;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author benfeihu
 */
@Controller
public class HelloController {

    @Autowired
    Car car;

    @RequestMapping("/hello")
    @ResponseBody
    private String hello() {
        return "hello, springboot01 ~";
    }

    @RequestMapping("/car")
    @ResponseBody
    private String car() {
        return String.valueOf(car);
    }

}
