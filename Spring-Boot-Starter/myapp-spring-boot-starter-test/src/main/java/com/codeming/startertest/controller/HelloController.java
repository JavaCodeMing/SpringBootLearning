package com.codeming.startertest.controller;

import com.codeming.starter.HelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author dengzhiming
 * @date 2020/3/5 23:50
 */
@RestController
public class HelloController {
    @Autowired
    HelloService helloService;

    @GetMapping("/hello")
    public String sayHello(String name) {
        return helloService.sayHello(name);
    }
}
