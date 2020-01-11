package com.example.websocket.listener;

import com.example.websocket.model.ChatMessage;
import com.example.websocket.service.ChatService;
import com.example.websocket.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.stereotype.Component;

/**
 * Redis订阅频道监听类
 * @author dengzhiming
 * @date 2020/01/11
 */
@Component
public class RedisMessageListener extends MessageListenerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisMessageListener.class);

    @Value("${redis.channel.msgToAll}")
    private String msgToAll;
    @Value("${redis.channel.userStatus}")
    private String userStatus;

    private final RedisTemplate<String, String> redisTemplate;
    private final ChatService chatService;
    public RedisMessageListener(RedisTemplate<String, String> redisTemplate, ChatService chatService) {
        this.redisTemplate = redisTemplate;
        this.chatService = chatService;
    }

    /**
     * 监听到消息
     * @param message 消息对象
     * @param bytes 匹配通道的模式的字节数组
     */
    @Override
    public void onMessage(Message message, byte[] bytes) {
        byte[] body = message.getBody();
        byte[] channel = message.getChannel();
        String rawMsg;
        String topic;
        try {
            // 将存储到redis中的消息反序列化成字符串
            rawMsg = redisTemplate.getStringSerializer().deserialize(body);
            // 将redis中的监听通道反序列化成字符串
            topic = redisTemplate.getStringSerializer().deserialize(channel);
            LOGGER.info("Received raw message from topic:" + topic + ", raw message content：" + rawMsg);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return;
        }

        //根据监听通道的不同,调用不同接口给前端返回相应数据
        if (msgToAll.equals(topic)) {
            LOGGER.info("Send message to all users:" + rawMsg);
            ChatMessage chatMessage = JsonUtil.parseJsonToObj(rawMsg, ChatMessage.class);
            if (chatMessage != null) {
                chatService.sendMsg(chatMessage);
            }
        } else if (userStatus.equals(topic)) {
            ChatMessage chatMessage = JsonUtil.parseJsonToObj(rawMsg, ChatMessage.class);
            if (chatMessage != null) {
                chatService.alertUserStatus(chatMessage);
            }
        }else {
            LOGGER.warn("No further operation with this topic!");
        }
    }
}
