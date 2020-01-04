package com.example.quartz.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Created by dengzhiming on 2019/8/1
 */
public interface BaseJob extends Job {
    @Override
    void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException;
}
