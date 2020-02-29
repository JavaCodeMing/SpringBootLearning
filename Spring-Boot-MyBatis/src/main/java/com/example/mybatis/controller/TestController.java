package com.example.mybatis.controller;

import com.example.mybatis.bean.Student;
import com.example.mybatis.service.StudentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * Created by dengzhiming on 2019/3/21
 */
@RestController
public class TestController {
    @Resource
    private StudentService service;

    @GetMapping("/querystudent/{sno}")
    public Student queryStudentBySno(@PathVariable String sno) {
        return this.service.queryStudentById(sno);
    }

}
