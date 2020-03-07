package com.example.multidatasource.service.impl;

import com.example.multidatasource.mysqldao.MysqlStudentMapper;
import com.example.multidatasource.oracledao.OracleStudentMapper;
import com.example.multidatasource.service.StudentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * Created by dengzhiming on 2019/3/28
 */
@Service
public class StudentServiceImpl implements StudentService {
    @Resource
    private OracleStudentMapper oracleStudentMapper;
    @Resource
    private MysqlStudentMapper mysqlStudentMapper;

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
