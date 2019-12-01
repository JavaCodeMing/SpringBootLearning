package com.example.corssupport.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by dengzhiming on 2019/6/30
 */
@Controller
public class TestController {
    @GetMapping("index")
    public String index() {
        return "index";
    }

    @GetMapping("hello")
    @ResponseBody
    //@CrossOrigin(value = "*")
    public String hello() {
        return "hello";
    }
}
