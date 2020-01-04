package com.example.quartz.service.impl;

import com.example.quartz.dao.JobAndTriggerMapper;
import com.example.quartz.entity.JobAndTrigger;
import com.example.quartz.job.BaseJob;
import com.example.quartz.service.JobAndTriggerService;
import org.apache.commons.lang3.StringUtils;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by dengzhiming on 2019/8/1
 */
@Service
public class JobAndTriggerServiceImpl implements JobAndTriggerService {

    private final JobAndTriggerMapper mapper;
    private final Scheduler scheduler;
    private static Logger logger = LoggerFactory.getLogger(JobAndTriggerServiceImpl.class);
    public JobAndTriggerServiceImpl(JobAndTriggerMapper mapper, @Qualifier("scheduler") Scheduler scheduler) {
        this.mapper = mapper;
        this.scheduler = scheduler;
    }

    @Override
    public List<JobAndTrigger> getTriggerAndDetails() {
        return mapper.getTriggerAndDetails();
    }

    @Override
    public void addjob(JobAndTrigger jobAndTrigger) {
        try {
            if (StringUtils.isNoneEmpty(
                    jobAndTrigger.getJobName()
                    , jobAndTrigger.getJobClassName()
                    , jobAndTrigger.getJobGroup()
                    , jobAndTrigger.getTriggerName()
                    , jobAndTrigger.getTriggerGroup())) {
                if (StringUtils.isNoneEmpty(jobAndTrigger.getCronExpression())) {
                    addCronJob(jobAndTrigger);
                } else {
                    addSimpleJob(jobAndTrigger);
                }
            }
        } catch (Exception e) {
            logger.info("创建定时任务失败");
            e.printStackTrace();
        }
    }

    //CronTrigger
    private void addCronJob(JobAndTrigger jobAndTrigger) throws Exception {
        // 启动Scheduler
        scheduler.start();

        // 构建JobDetail
        JobDetail jobDetail = JobBuilder.newJob(getClass(jobAndTrigger.getJobClassName()).getClass())
                .withIdentity(jobAndTrigger.getJobName(), jobAndTrigger.getJobGroup())
                .build();
        // 根据参数中的Cron表达式构建ScheduleBuilder
        CronScheduleBuilder cronSchedule = CronScheduleBuilder.cronSchedule(jobAndTrigger.getCronExpression());
        if (!StringUtils.isEmpty(jobAndTrigger.getTimeZoneId())) {
            cronSchedule.inTimeZone(TimeZone.getTimeZone(jobAndTrigger.getTimeZoneId()));
        }
        // 构建CronTrigger
        CronTrigger cronTrigger = TriggerBuilder.newTrigger()
                .withIdentity(jobAndTrigger.getTriggerName(), jobAndTrigger.getTriggerGroup())
                .withSchedule(cronSchedule)
                .build();
        // 将jobDetail和CronTrigger绑定,并纳入到Scheduler中
        scheduler.scheduleJob(jobDetail, cronTrigger);
    }

    //SimpleTrigger
    private void addSimpleJob(JobAndTrigger jobAndTrigger) throws Exception {
        // 启动Scheduler
        scheduler.start();
        // 构建JobDetail
        JobDetail jobDetail = JobBuilder.newJob(getClass(jobAndTrigger.getJobClassName()).getClass())
                .withIdentity(jobAndTrigger.getJobName(), jobAndTrigger.getJobGroup())
                .build();
        // 构建SimpleTrigger
        SimpleTrigger simpleTrigger = TriggerBuilder.newTrigger()
                .withIdentity(jobAndTrigger.getTriggerName(), jobAndTrigger.getTriggerGroup())
                .startAt(jobAndTrigger.getStartTime())
                .withSchedule(
                        SimpleScheduleBuilder.simpleSchedule()
                                .withIntervalInMilliseconds(jobAndTrigger.getRepeatInterval())
                                .withRepeatCount(jobAndTrigger.getRepeatCount()))
                .build();
        // 将jobDetail和simpleTrigger绑定,并纳入到Scheduler中
        scheduler.scheduleJob(jobDetail, simpleTrigger);
    }

    @Override
    public void pausejob(String triggerName, String triggerGroup) {
        try {
            scheduler.pauseTrigger(TriggerKey.triggerKey(triggerName, triggerGroup));
        } catch (SchedulerException e) {
            logger.info("定时任务暂停失败");
            e.printStackTrace();
        }
    }

    @Override
    public void resumejob(String triggerName, String triggerGroup) {
        try {
            scheduler.resumeTrigger(TriggerKey.triggerKey(triggerName, triggerGroup));
        } catch (SchedulerException e) {
            logger.info("定时任务恢复失败");
            e.printStackTrace();
        }
    }

    @Override
    public void deletejob(String triggerName, String triggerGroup) {
        try {
            scheduler.unscheduleJob(TriggerKey.triggerKey(triggerName, triggerGroup));
        } catch (SchedulerException e) {
            logger.info("定时任务删除失败");
            e.printStackTrace();
        }
    }

    @Override
    public void reschedulejob(JobAndTrigger jobAndTrigger) {
        try {
            if (StringUtils.isNoneEmpty(
                    jobAndTrigger.getTriggerName()
                    , jobAndTrigger.getTriggerGroup())) {
                if (StringUtils.isNoneEmpty(jobAndTrigger.getCronExpression())) {
                    updateCronJob(jobAndTrigger);
                    return;
                }
                if (jobAndTrigger.getStartTime() != null
                        || jobAndTrigger.getRepeatCount() != null
                        || jobAndTrigger.getRepeatInterval() != null
                        || jobAndTrigger.getEndTime() != null) {
                    updateSimpleJob(jobAndTrigger);
                }
            }
        } catch (SchedulerException e) {
            logger.info("定时任务更新失败");
            e.printStackTrace();
        }
    }

    private void updateCronJob(JobAndTrigger jobAndTrigger) throws SchedulerException {
        TriggerKey triggerKey = TriggerKey.triggerKey(jobAndTrigger.getTriggerName(), jobAndTrigger.getTriggerGroup());
        // 根据参数中的Cron表达式构建ScheduleBuilder
        CronScheduleBuilder cronSchedule = CronScheduleBuilder.cronSchedule(jobAndTrigger.getCronExpression());
        CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
        CronTrigger cronTrigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(cronSchedule).build();
        scheduler.rescheduleJob(triggerKey, cronTrigger);
    }

    private void updateSimpleJob(JobAndTrigger jobAndTrigger) throws SchedulerException {
        Date startTime = jobAndTrigger.getStartTime();
        Integer count = jobAndTrigger.getRepeatCount();
        Long interval = jobAndTrigger.getRepeatInterval();
        Date endTime = jobAndTrigger.getEndTime();
        TriggerKey triggerKey = TriggerKey.triggerKey(jobAndTrigger.getTriggerName(), jobAndTrigger.getTriggerGroup());
        SimpleTrigger trigger = (SimpleTrigger) scheduler.getTrigger(triggerKey);
        SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder.simpleSchedule();
        if (count != null) {
            scheduleBuilder.withRepeatCount(count);
        }
        if (interval != null) {
            scheduleBuilder.withIntervalInMilliseconds(interval);
        }
        TriggerBuilder<SimpleTrigger> triggerBuilder = trigger.getTriggerBuilder().withIdentity(triggerKey);
        if (startTime != null) {
            triggerBuilder.startAt(startTime);
        }
        if (endTime != null) {
            triggerBuilder.endAt(endTime);
        }
        if (count != null || interval != null) {
            triggerBuilder.withSchedule(scheduleBuilder);
        }
        scheduler.rescheduleJob(triggerKey, triggerBuilder.build());
    }

    private BaseJob getClass(String className) throws Exception {
        Class<?> clazz = Class.forName(className);
        return (BaseJob) clazz.newInstance();
    }

}
