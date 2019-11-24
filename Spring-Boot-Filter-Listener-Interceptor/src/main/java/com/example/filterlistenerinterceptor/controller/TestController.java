package com.example.filterlistenerinterceptor.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by dengzhiming on 2019/5/31
 */
@RestController
public class TestController {

    @GetMapping("/test")
    public String test() {
        System.out.println("controller 执行");
        return "controller return";
    }
}
