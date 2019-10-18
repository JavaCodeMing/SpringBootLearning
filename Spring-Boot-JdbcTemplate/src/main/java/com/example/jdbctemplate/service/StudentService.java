package com.example.jdbctemplate.service;

import com.example.jdbctemplate.bean.Student;

import java.util.List;
import java.util.Map;

/**
 * Created by dengzhiming on 2019/3/24
 */
public interface StudentService {
    int add(Student student);

    int update(Student student);

    int deleteBysno(String sno);

    List<Map<String, Object>> queryStudentListMap();

    Student queryStudentBySno(String sno);
}
