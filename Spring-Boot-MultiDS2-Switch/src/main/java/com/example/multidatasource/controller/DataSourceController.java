package com.example.multidatasource.controller;

import com.example.multidatasource.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Created by dengzhiming on 2019/4/7
 */
@RestController
public class DataSourceController {
    private final StudentService studentService;

    @Autowired
    public DataSourceController(StudentService studentService) {
        this.studentService = studentService;
    }

    @RequestMapping("/querystudentsfromoracle")
    public List<Map<String, Object>> queryStudentsFromOracle() {
        return this.studentService.getAllStudentsFromSlave();
    }

    @RequestMapping("/querystudentsfrommysql")
    public List<Map<String, Object>> queryStudentsFromMysql() {
        return this.studentService.getAllStudentsFromMaster();
    }
}
