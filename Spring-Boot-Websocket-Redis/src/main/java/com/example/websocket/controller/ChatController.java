package com.example.websocket.controller;

import com.example.websocket.model.ChatMessage;
import com.example.websocket.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

/**
 * @author dengzhiming
 * @date 2020/01/11
 */
@Controller
public class ChatController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChatController.class);

    @Value("${redis.channel.msgToAll}")
    private String msgToAll;
    @Value("${redis.set.onlineUsers}")
    private String onlineUsers;
    @Value("${redis.channel.userStatus}")
    private String userStatus;

    private final RedisTemplate<String, String> redisTemplate;
    public ChatController(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 前端websocket请求发送消息路由到此方法
     * @param chatMessage 消息对象
     */
    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessage chatMessage) {
        try {
            // 将消息存储到redis中
            redisTemplate.convertAndSend(msgToAll, JsonUtil.parseObjToJson(chatMessage));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * 用户加入时添加该用户名到 websocket session,并将用户名和用户状态存入redis中
     * @param chatMessage 消息对象
     * @param headerAccessor 消息头访问器对象
     */
    @MessageMapping("/chat.addUser")
    public void addUser(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        LOGGER.info("User added in Chatroom:" + chatMessage.getSender());
        try {
            headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
            // 用户加入,将该用户添加到redis用户集合中
            redisTemplate.opsForSet().add(onlineUsers, chatMessage.getSender());
            // 用户加入,将该用户的用户状态添加到redis中
            redisTemplate.convertAndSend(userStatus, JsonUtil.parseObjToJson(chatMessage));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

}
