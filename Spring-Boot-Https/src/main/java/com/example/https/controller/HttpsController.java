package com.example.https.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author dengzhiming
 * @date 2020/3/5 21:11
 */
@RestController
public class HttpsController {
    @GetMapping(value = "/hello")
    public String hello() {
        return "Hello HTTPS";
    }
}
