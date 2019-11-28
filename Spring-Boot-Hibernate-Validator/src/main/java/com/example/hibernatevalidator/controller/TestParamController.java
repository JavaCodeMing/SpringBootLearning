package com.example.hibernatevalidator.controller;

import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

/**
 * Created by dengzhiming on 2019/6/29
 */
@Controller
@Validated
public class TestParamController {
    @GetMapping("/test1")
    @ResponseBody
    public String test1(
            @NotNull(message = "{required}") String name,
            @Email(message = "{invalid}") String email) {
        return "success";
    }
}
