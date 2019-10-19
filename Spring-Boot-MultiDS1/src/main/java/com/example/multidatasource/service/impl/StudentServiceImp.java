package com.example.multidatasource.service.impl;

import com.example.multidatasource.mysqldao.MysqlStudentMapper;
import com.example.multidatasource.oracledao.OracleStudentMapper;
import com.example.multidatasource.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;

/**
 * Created by dengzhiming on 2019/3/28
 */
@Service("studentService")
public class StudentServiceImp implements StudentService {
    private final OracleStudentMapper oracleStudentMapper;
    private final MysqlStudentMapper mysqlStudentMapper;

    @Autowired
    public StudentServiceImp(OracleStudentMapper oracleStudentMapper, MysqlStudentMapper mysqlStudentMapper) {
        this.oracleStudentMapper = oracleStudentMapper;
        this.mysqlStudentMapper = mysqlStudentMapper;
    }

    @Override
    @Transactional
    public List<Map<String, Object>> getAllStudentsFromMysql() {
        return this.mysqlStudentMapper.getAllStudents();
    }

    @Override
    @Transactional("oracleTransactionManager")
    public List<Map<String, Object>> getAllStudentsFromOracle() {
        return this.oracleStudentMapper.getAllStudents();
    }
}
