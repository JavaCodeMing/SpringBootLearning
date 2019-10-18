package com.example.mybatis.controller;

import com.example.mybatis.bean.Student;
import com.example.mybatis.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by dengzhiming on 2019/3/21
 */
@RestController
public class TestController {
    @Autowired
    private StudentService service;

    @RequestMapping(value = "/querystudent", method = RequestMethod.GET)
    public Student queryStudentBySno(String sno) {
        return this.service.queryStudentById(sno);
    }

}
