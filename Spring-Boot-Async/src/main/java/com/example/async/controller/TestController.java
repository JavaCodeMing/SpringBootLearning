package com.example.async.controller;

import com.example.async.service.TestReturnService;
import com.example.async.service.TestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by dengzhiming on 2019/6/30
 */
@RestController
public class TestController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private final TestService testService;
    private final TestReturnService testReturnService;

    @Autowired
    public TestController(TestService testService, TestReturnService testReturnService) {
        this.testService = testService;
        this.testReturnService = testReturnService;
    }

    @GetMapping("async")
    public void testAsync() {
        long start = System.currentTimeMillis();
        logger.info("异步方法开始");
        testService.asyncMethod();
        logger.info("异步方法结束");
        long end = System.currentTimeMillis();
        logger.info("总耗时:{} ms ", end - start);
    }

    @GetMapping("sync")
    public void testSync() {
        long start = System.currentTimeMillis();
        logger.info("同步方法开始");
        testService.syncMethod();
        logger.info("同步方法结束");
        long end = System.currentTimeMillis();
        logger.info("总耗时:{} ms ", end - start);
    }

    @GetMapping("asyncReturn")
    public String testAsyncReturn() throws ExecutionException, InterruptedException {
        long start = System.currentTimeMillis();
        logger.info("异步方法开始");
        Future<String> stringFuture = testReturnService.asyncReturnMethod();
        String result = stringFuture.get();
        logger.info("异步方法返回值：{}", result);
        logger.info("异步方法结束");
        long end = System.currentTimeMillis();
        logger.info("总耗时：{} ms", end - start);
        return result;
    }
}
