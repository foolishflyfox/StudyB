package com.bfh.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author benfeihu
 */
@Controller
public class HelloController {

    @ResponseBody // 表示返回是直接给浏览器的
    @RequestMapping("/hello")  // 接收来自于浏览器的 hello 请求
    public String handler01() {
        return "hello, spring boot!";
    }
}
