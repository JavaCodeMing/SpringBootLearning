package com.example.quartz.config;

import org.quartz.Scheduler;
import org.quartz.ee.servlet.QuartzInitializerListener;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by dengzhiming on 2019/8/1
 */
@Configuration
public class SchedulerConfig {
    @Bean
    public Properties quartzProperties() throws IOException {
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        // 通过ClassPathResource将属性文件转化Resource对象,并交由PropertiesFactoryBean管理
        propertiesFactoryBean.setLocation(new ClassPathResource("/quartz.properties"));
        //在quartz.properties中的属性被读取并注入后再初始化对象
        propertiesFactoryBean.afterPropertiesSet();
        return propertiesFactoryBean.getObject();
    }

    @Bean("schedulerFactory")
    public SchedulerFactoryBean schedulerFactoryBean() throws IOException {
        SchedulerFactoryBean factoryBean = new SchedulerFactoryBean();
        factoryBean.setQuartzProperties(quartzProperties());
        return factoryBean;
    }

    /**
     * quartz初始化监听器
     * 这个监听器可以监听到工程的启动,在工程停止再启动时可以让已有的定时任务继续进行
     */
    @Bean
    public QuartzInitializerListener initializerListener() {
        return new QuartzInitializerListener();
    }

    /**
     * 通过SchedulerFactoryBean获取Scheduler的实例
     */
    @Bean("scheduler")
    public Scheduler scheduler() throws IOException {
        return schedulerFactoryBean().getScheduler();
    }
}
