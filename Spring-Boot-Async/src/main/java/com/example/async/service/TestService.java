package com.example.async.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Created by dengzhiming on 2019/6/30
 */
@Service
public class TestService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Async
    public void asyncMethod() {
        sleep();
        logger.info("异步方法内部线程名称：{}", Thread.currentThread().getName());
    }

    public void syncMethod() {
        sleep();
    }

    private void sleep() {
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
