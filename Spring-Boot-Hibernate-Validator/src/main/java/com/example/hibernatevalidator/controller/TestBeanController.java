package com.example.hibernatevalidator.controller;

import com.example.hibernatevalidator.bean.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;

/**
 * Created by dengzhiming on 2019/6/29
 */
@Controller
public class TestBeanController {
    @GetMapping("test2")
    @ResponseBody
    public String test2(@Valid User user) {
        return "success";
    }
}
