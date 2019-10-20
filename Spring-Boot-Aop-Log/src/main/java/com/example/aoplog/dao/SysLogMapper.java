package com.example.aoplog.dao;

import com.example.aoplog.bean.SysLog;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * Created by dengzhiming on 2019/4/11
 */
@Repository
@Mapper
public interface SysLogMapper {
    void saveSysLog(SysLog syslog);
}

