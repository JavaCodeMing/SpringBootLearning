package com.example.jdbctemplate.controller;

import com.example.jdbctemplate.bean.Student;
import com.example.jdbctemplate.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Created by dengzhiming on 2019/3/24
 */
@RestController
public class TestController {
    @Autowired
    private StudentService studentService;

    @GetMapping("/querystudent/{sno}")
    public Student queryStudentById(@PathVariable String sno) {
        return this.studentService.queryStudentBySno(sno);
    }

    @GetMapping("/queryallstudent")
    public List<Map<String, Object>> queryAllStudent() {
        return this.studentService.queryStudentListMap();
    }

    @PostMapping("/addstudent")
    public int saveStudent(@RequestBody Student student) {
        return this.studentService.add(student);
    }

    @PutMapping("/updatestudent")
    public int updatestudent(@RequestBody Student student){
        return this.studentService.update(student);
    }

    @DeleteMapping("/deletestudent/{sno}")
    public int deleteStudentById(@PathVariable String sno) {
        return this.studentService.deleteBysno(sno);
    }
}
