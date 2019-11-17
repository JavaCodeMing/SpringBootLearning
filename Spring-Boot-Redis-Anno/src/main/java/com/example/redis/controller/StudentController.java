package com.example.redis.controller;

import com.example.redis.bean.Student;
import com.example.redis.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by dengzhiming on 2019/11/17
 */
@RestController
public class StudentController {

    private final StudentService studentService;

    @Autowired
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @RequestMapping("/update")
    public String update(Student student){
        try {
            studentService.update(student);
            return "success";
        }catch (Exception e){
            e.printStackTrace();
        }
        return "false";
    }

    @RequestMapping("/delete")
    public String deleteById(@RequestParam("sno") String sno){
        try {
            studentService.deleteById(sno);
            return "success";
        }catch (Exception e){
            e.printStackTrace();
        }
        return "false";
    }

    @RequestMapping("/query")
    public Student queryStudentById(@RequestParam("sno") String sno){
        return studentService.queryStudentById(sno);
    }
}
