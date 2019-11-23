package com.example.xssfilter.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by dengzhiming on 2019/5/25
 */
@Controller
public class TestController {

    @PostMapping("/testParam")
    @ResponseBody
    public String testParam(@RequestParam String param) {
        return param;
    }
}
