package com.example.aoplog.controller;

import com.example.aoplog.annotation.Log;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by dengzhiming on 2019/4/11
 */
@RestController
public class TestController {

    @Log("查询的方法")
    @GetMapping("/query")
    public void query() throws InterruptedException {
        Thread.sleep(200);
    }

    @Log("新增的方法")
    @PostMapping("/add")
    public void add(String name, int age, char sex) throws InterruptedException {
        Thread.sleep(500);
    }

    @Log("删除的方法")
    @DeleteMapping("/delete")
    public void delete(String name) throws InterruptedException {
        Thread.sleep(100);
    }
}
