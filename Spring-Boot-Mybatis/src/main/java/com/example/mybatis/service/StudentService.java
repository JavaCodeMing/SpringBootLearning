package com.example.mybatis.service;

import com.example.mybatis.bean.Student;

/**
 * Created by dengzhiming on 2019/3/21
 */
public interface StudentService {
    int add(Student student);

    int update(Student student);

    int deleteById(String sno);

    Student queryStudentById(String sno);
}
