package com.example.quartz.job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Created by dengzhiming on 2019/8/1
 */
public class NewJob implements BaseJob {

    private static Logger logger = LoggerFactory.getLogger(NewJob.class);

    public NewJob() {
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        logger.info("NewJob执行的时间: " + new Date());
    }
}
