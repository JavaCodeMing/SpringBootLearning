package com.example.quartz.dao;

import com.example.quartz.entity.JobAndTrigger;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by dengzhiming on 2019/8/1
 */
@Repository
@Mapper
public interface JobAndTriggerMapper {
    List<JobAndTrigger> getTriggerAndDetails();
}
