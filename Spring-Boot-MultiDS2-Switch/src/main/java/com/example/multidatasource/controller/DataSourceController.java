package com.example.multidatasource.controller;

import com.example.multidatasource.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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

    @GetMapping("/querystudentsfromoracle")
    public List<Map<String, Object>> queryStudentsFromOracle() {
        return studentService.getAllStudentsFromSlave();
    }

    @PostMapping("/updateStudent/{sno}/{sname}")
    public String updateStudent(@PathVariable("sno") int sno, @PathVariable("sname") String sname) {
        int i = studentService.updateStudent(sno, sname);
        if (i == -1) {
            return "failure";
        } else {
            return "success";
        }
    }

    @GetMapping("/querystudentsfrommysql")
    public List<Map<String, Object>> queryStudentsFromMysql() {
        return studentService.getAllStudentsFromMaster();
    }
}
