package com.example.async.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by dengzhiming on 2019/6/30
 */
@Configuration
public class AsyncPoolConfig {

    @Bean
    public ThreadPoolTaskExecutor asyncThreadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 线程池维护线程的最少数量
        executor.setCorePoolSize(20);
        // 线程池维护线程的最大数量,只有在缓冲队列满了之后才会申请超过核心线程数的线程
        executor.setMaxPoolSize(60);
        // 缓存队列
        executor.setQueueCapacity(25);
        // 线程池中线程名字的前缀
        executor.setThreadNamePrefix("asynTaskThread-");
        // 允许的空闲时间,当超过了核心线程出之外的线程在空闲时间到达之后会被销毁
        executor.setKeepAliveSeconds(200);
        // 等待所有线程执行完再退出
        executor.setWaitForTasksToCompleteOnShutdown(true);
        // 等待终止的时间,因为不能无限的等待下去
        executor.setAwaitTerminationSeconds(60);
        // 线程池对拒绝任务(无线程可用)的处理策略
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 线程池初始化
        executor.initialize();
        return executor;
    }
}
