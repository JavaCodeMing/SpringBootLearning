package com.example.websocket.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * @author dengzhiming
 * @date 2020/01/11
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * STOMP代表简单文本导向的消息传递协议;它是一种消息传递协议,用于定义数据交换的格式和规则;
     * @param registry 消息传递协议断点注册器
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        //注册一个websocket端点,供客户端连接到websocket服务器
        registry.addEndpoint("/ws")
                //设置为"*"表示接收 http 和 https 的请求
                .setAllowedOrigins("*")
                //作为不支持websocket的浏览器的后备选项,使用了SockJS
                .withSockJS();
    }

    /**
     * 配置一个消息代理,用于将消息从一个客户端路由到另一个客户端
     * @param registry 消息代理的注册器
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        //以"/app"开头的消息应该路由到消息处理方法;
        registry.setApplicationDestinationPrefixes("/app");
        //以"/topic"开头的消息应该路由到消息代理;消息代理向订阅特定主题的所有连接客户端广播消息;
        registry.enableSimpleBroker("/topic");
        //使用它来启用一个功能完整的代理,如RabbitMQ
        /*registry.enableStompBrokerRelay("/topic")
                .setRelayHost("localhost")
                .setRelayPort(61613)
                .setClientLogin("guest")
                .setClientPasscode("guest");*/
    }
}