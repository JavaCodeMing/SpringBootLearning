package com.example.quartz.service;

import com.example.quartz.entity.JobAndTrigger;

import java.util.List;

/**
 * Created by dengzhiming on 2019/8/1
 */
public interface JobAndTriggerService {
    List<JobAndTrigger> getTriggerAndDetails();

    void addjob(JobAndTrigger jobAndTrigger);

    void pausejob(String triggerName, String triggerGroup);

    void resumejob(String triggerName, String triggerGroup);

    void deletejob(String triggerName, String triggerGroup);

    void reschedulejob(JobAndTrigger jobAndTrigger);
}
