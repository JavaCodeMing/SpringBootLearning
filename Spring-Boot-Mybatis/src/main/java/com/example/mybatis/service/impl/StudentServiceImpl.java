package com.example.mybatis.service.impl;

import com.example.mybatis.bean.Student;
import com.example.mybatis.mapper.StudentMapper;
import com.example.mybatis.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by dengzhiming on 2019/3/21
 */
@Service
public class StudentServiceImpl implements StudentService {
    @Autowired
    private StudentMapper studentMapper;

    @Override
    public int add(Student student) {
        return this.studentMapper.add(student);
    }

    @Override
    public int update(Student student) {
        return this.studentMapper.update(student);
    }

    @Override
    public int deleteById(String sno) {
        return this.studentMapper.deleteById(sno);
    }

    @Override
    public Student queryStudentById(String sno) {
        return this.studentMapper.queryStudentById(sno);
    }
}
