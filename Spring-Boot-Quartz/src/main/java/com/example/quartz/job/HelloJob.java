package com.example.quartz.job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Created by dengzhiming on 2019/8/1
 */
public class HelloJob implements BaseJob {

    private static Logger logger = LoggerFactory.getLogger(HelloJob.class);

    public HelloJob() {
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        logger.info("HelloJob执行时间: " + new Date());
    }
}
