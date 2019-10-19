package com.example.multidatasource.controller;

import com.example.multidatasource.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Map;

/**
 * Created by dengzhiming on 2019/3/28
 */
@RestController
public class StudentController {
    private final StudentService studentService;

    @Autowired
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @RequestMapping("/querystudentsfromoracle")
    public List<Map<String, Object>> queryStudentsFromOracle() {
        return this.studentService.getAllStudentsFromOracle();
    }

    @RequestMapping("/querystudentsfrommysql")
    public List<Map<String, Object>> queryStudentsFromMysql() {
        return this.studentService.getAllStudentsFromMysql();
    }
}
