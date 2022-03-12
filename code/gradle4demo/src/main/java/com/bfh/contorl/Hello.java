package com.bfh.contorl;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author benfeihu
 */
@Controller
public class Hello {
    @ResponseBody
    @RequestMapping
    public String hello() {
        return "hello, jar world!";
    }
}
