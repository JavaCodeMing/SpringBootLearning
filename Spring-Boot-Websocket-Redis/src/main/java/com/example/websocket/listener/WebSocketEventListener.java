package com.example.websocket.listener;

import com.example.websocket.model.ChatMessage;
import com.example.websocket.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.net.Inet4Address;
import java.net.InetAddress;


/**
 * WebSocket的事件监听器
 * @author dengzhiming
 * @date 2020/01/11
 */
@Component
public class WebSocketEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketEventListener.class);

    @Value("${server.port}")
    private String serverPort;
    @Value("${redis.set.onlineUsers}")
    private String onlineUsers;
    @Value("${redis.channel.userStatus}")
    private String userStatus;

    private final RedisTemplate<String, String> redisTemplate;
    public WebSocketEventListener(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 用户连接触发的监听方法
     * @param event 连接事件对象
     */
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        try {
            InetAddress localHost = Inet4Address.getLocalHost();
            LOGGER.info("Received a new web socket connection from:" + localHost.getHostAddress() + ":" + serverPort);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

    }

    /**
     * 用户退出触发的监听方法
     * @param event 断开连接事件对象
     */
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = (String) headerAccessor.getSessionAttributes().get("username");
        if(username != null) {
            LOGGER.info("User Disconnected : " + username);
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setType(ChatMessage.MessageType.LEAVE);
            chatMessage.setSender(username);
            try {
                // 用户离开,将该用户从redis用户集合中删除
                redisTemplate.opsForSet().remove(onlineUsers, username);
                // 用户离开,给redis发送该用户变更用户状态的消息
                redisTemplate.convertAndSend(userStatus, JsonUtil.parseObjToJson(chatMessage));
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }
}
