package com.example.elasticsearch.config;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RestClientConfig {
  @Value("${elasticsearch.hostname}")
  private String hostname;
  @Value("${elasticsearch.port}")
  private int port;

  /**
   * LowLevelRestConfig
   *
   * @return org.elasticsearch.client.RestClient
   */
  @Bean
  public RestClient restClient() {
    // 如果有多个从节点可以持续在内部new多个HttpHost，参数1是IP，参数2是端口，参数3是通信协议
    RestClientBuilder clientBuilder = RestClient.builder(new HttpHost(hostname, port, "http"));
    // 设置Header编码
    Header[] defaultHeaders = {new BasicHeader("content-type", "application/json")};
    clientBuilder.setDefaultHeaders(defaultHeaders);
    // 添加其他配置，这些配置都是可选的，详情配置可看https://blog.csdn.net/jacksonary/article/details/82729556
    return clientBuilder.build();
  }

  /**
   * HighLevelRestConfig
   *
   * @return org.elasticsearch.client.RestClient
   */
  @Bean
  public RestHighLevelClient restHighLevelClient() {
    // 如果有多个从节点可以持续在内部new多个HttpHost，参数1是IP，参数2是端口，参数3是通信协议
    return new RestHighLevelClient(RestClient.builder(new HttpHost(hostname, port, "http")));
  }

}
