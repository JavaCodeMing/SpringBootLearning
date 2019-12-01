package com.example.async.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by dengzhiming on 2019/6/30
 */
@Service
public class TestReturnService {
    //底层默认使用logback
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Async("asyncThreadPoolTaskExecutor")
    public Future<String> asyncReturnMethod() {
        sleep();
        logger.info("异步方法内部线程名称：{}", Thread.currentThread().getName());
        return new AsyncResult<>("hello async");
    }

    private void sleep() {
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
