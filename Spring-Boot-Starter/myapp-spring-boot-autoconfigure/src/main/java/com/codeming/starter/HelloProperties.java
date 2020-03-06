package com.codeming.starter;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author dengzhiming
 * @date 2020/3/5 23:08
 */
@ConfigurationProperties(prefix = "myapp.hello")
public class HelloProperties {
    private String suffix;

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }
}
