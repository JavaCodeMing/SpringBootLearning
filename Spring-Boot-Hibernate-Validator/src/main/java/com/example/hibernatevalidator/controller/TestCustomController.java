package com.example.hibernatevalidator.controller;

import com.example.hibernatevalidator.config.MyConstraint;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by dengzhiming on 2019/6/29
 */
@Controller
@Validated
public class TestCustomController {
    @GetMapping("test3")
    @ResponseBody
    public String test3(@MyConstraint(message = "{illegal}") String name) {
        return "success";
    }
}
