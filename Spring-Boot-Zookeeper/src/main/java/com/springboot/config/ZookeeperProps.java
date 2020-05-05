package com.springboot.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author dengzhiming
 * @date 2020/5/5 14:10
 */
@Data
@Component
@ConfigurationProperties(prefix = "zookeeper")
public class ZookeeperProps {
    private boolean enabled ;
    private String server ;
    private String namespace ;
    private String digest ;
    private Integer sessionTimeoutMs ;
    private Integer connectionTimeoutMs ;
    private Integer maxRetries ;
    private Integer baseSleepTimeMs ;
}
