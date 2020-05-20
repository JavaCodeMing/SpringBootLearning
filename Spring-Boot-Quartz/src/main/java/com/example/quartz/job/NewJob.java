package com.example.quartz.job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Created by dengzhiming on 2019/8/1
 */
public class NewJob extends BaseJob {

    private static Logger logger = LoggerFactory.getLogger(NewJob.class);
    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        logger.info("NewJob执行的时间: " + new Date());
    }
}
