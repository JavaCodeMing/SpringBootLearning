package com.example.multidatasource.dao;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Created by dengzhiming on 2019/4/7
 */
@Repository
@Mapper
public interface StudentMapper {
    List<Map<String, Object>> getAllStudents();
}
