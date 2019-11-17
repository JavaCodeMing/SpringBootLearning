package com.example.ehcache.service.impl;

import com.example.ehcache.bean.Student;
import com.example.ehcache.dao.StudentMapper;
import com.example.ehcache.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * Created by dengzhiming on 2019/4/22
 */
@Service("studentService")
@CacheConfig(cacheNames = "student")
public class StudentServiceImpl implements StudentService {
    private final StudentMapper studentMapper;

    @Autowired
    public StudentServiceImpl(StudentMapper studentMapper) {
        this.studentMapper = studentMapper;
    }

    @CachePut(key = "#p0.sno")
    @Override
    public Student add(Student student) {
        this.studentMapper.add(student);
        return this.studentMapper.queryStudentById(student.getSno());
    }

    @CachePut(key = "#p0.sno")
    @Override
    public Student update(Student student) {
        this.studentMapper.update(student);
        return this.studentMapper.queryStudentById(student.getSno());
    }

    @CacheEvict(key = "#p0", allEntries = true)
    @Override
    public void deleteById(String sno) {
        this.studentMapper.deleteById(sno);
    }

    @Cacheable(key = "#p0")
    @Override
    public Student queryStudentById(String sno) {
        return this.studentMapper.queryStudentById(sno);
    }
}
