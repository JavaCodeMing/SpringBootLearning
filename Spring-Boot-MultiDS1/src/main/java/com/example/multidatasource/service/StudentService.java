package com.example.multidatasource.service;

import java.util.List;
import java.util.Map;

/**
 * Created by dengzhiming on 2019/3/28
 */
public interface StudentService {
    List<Map<String, Object>> getAllStudentsFromMysql();

    List<Map<String, Object>> getAllStudentsFromOracle();
}
