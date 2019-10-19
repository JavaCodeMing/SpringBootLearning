package com.example.multidatasource.service;

import java.util.List;
import java.util.Map;

/**
 * Created by dengzhiming on 2019/4/7
 */
public interface StudentService {
    List<Map<String, Object>> getAllStudentsFromMaster();

    List<Map<String, Object>> getAllStudentsFromSlave();
}