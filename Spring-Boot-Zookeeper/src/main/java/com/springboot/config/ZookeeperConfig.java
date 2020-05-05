package com.springboot.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @author dengzhiming
 * @date 2020/5/5 14:06
 */
@Slf4j
@Configuration
public class ZookeeperConfig {

    @Resource
    private ZookeeperProps zookeeperProps;

    @Bean(value="zkClient",initMethod="start")
    public CuratorFramework curatorFramework(){
        //重试策略，初试时间1秒，重试10次
        RetryPolicy policy = new ExponentialBackoffRetry(
                zookeeperProps.getBaseSleepTimeMs(),
                zookeeperProps.getMaxRetries());
        //通过工厂创建Curator
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString(zookeeperProps.getServer())
                .authorization("digest",zookeeperProps.getDigest().getBytes())
                .connectionTimeoutMs(zookeeperProps.getConnectionTimeoutMs())
                .sessionTimeoutMs(zookeeperProps.getSessionTimeoutMs())
                .retryPolicy(policy).build();
        log.info("zookeeper 初始化完成...");
        return client;
    }
}
