package com.example.redis.service;

import com.example.redis.bean.Student;

/**
 * Created by dengzhiming on 2019/4/17
 */
public interface StudentService {

    Student add(Student student);

    Student update(Student student);

    void deleteById(String sno);

    Student queryStudentById(String sno);
}
