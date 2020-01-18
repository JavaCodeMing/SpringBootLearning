package com.example.jasypt.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * Created by dengzhiming on 2020/1/18
 */
@RestController
@RequestMapping("/jasypt")
public class TestController {
    @Value("${name}")
    private String password;
    @GetMapping("/name")
    public Mono<String> sendNormalText() {
        System.out.println(password);
        return Mono.just(password);
    }
}
