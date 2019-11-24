package com.example.errorcontroller.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by dengzhiming on 2019/5/28
 */
@RestController
public class TestController {

    // 随机抛异常
    private void randomException() throws Exception {
        //异常集合
        Exception[] exceptions = {
                new NullPointerException(),
                new ArrayIndexOutOfBoundsException(),
                new NumberFormatException(),
                new SQLException()
        };
        //发生概率
        double probabity = 0.75;
        if (Math.random() < probabity) {
            throw exceptions[(int) (Math.random() * exceptions.length)];
        }
        //情况2: 继续运行
    }

    // 模拟用户数据访问
    @GetMapping("/test")
    public List<String> index() throws Exception {
        randomException();
        return Arrays.asList("正常用户数据1!", "正常用户数据2! 请按F5刷新!!");
    }

}
