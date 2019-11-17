package com.example.ehcache.dao;

import com.example.ehcache.bean.Student;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * Created by dengzhiming on 2019/4/17
 */
@Repository
@Mapper
public interface StudentMapper {
    int add(Student student);

    int update(Student student);

    int deleteById(String sno);

    Student queryStudentById(String id);
}
