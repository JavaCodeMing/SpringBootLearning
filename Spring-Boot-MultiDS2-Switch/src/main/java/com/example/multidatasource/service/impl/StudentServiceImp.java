package com.example.multidatasource.service.impl;

import com.example.multidatasource.dao.StudentMapper;
import com.example.multidatasource.datasourceConfig.TargetDataSource;
import com.example.multidatasource.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Created by dengzhiming on 2019/4/7
 */
@Service("studentService")
public class StudentServiceImp implements StudentService {
    private final StudentMapper studentMapper;

    @Autowired
    public StudentServiceImp(StudentMapper studentMapper) {
        this.studentMapper = studentMapper;
    }

    @Override
    public List<Map<String, Object>> getAllStudentsFromMaster() {
        return this.studentMapper.getAllStudents();
    }

    @Override
    @TargetDataSource("ds1")
    @Transactional
    public List<Map<String, Object>> getAllStudentsFromSlave() {
        return this.studentMapper.getAllStudents();
    }
}
