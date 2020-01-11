package com.example.websocket.service;

import com.example.websocket.model.ChatMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

/**
 * @author dengzhiming
 * @date 2020/01/11
 */
@Service
public class ChatService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChatService.class);
    private final SimpMessageSendingOperations simpMessageSendingOperations;
    public ChatService(SimpMessageSendingOperations simpMessageSendingOperations) {
        this.simpMessageSendingOperations = simpMessageSendingOperations;
    }

    /**
     * @param chatMessage 消息对象
     */
    public void sendMsg(@Payload ChatMessage chatMessage) {
        LOGGER.info("Send msg by simpMessageSendingOperations:" + chatMessage.toString());
        simpMessageSendingOperations.convertAndSend("/topic/public", chatMessage);
    }

    /**
     * @param chatMessage 消息对象
     */
    public void alertUserStatus(@Payload ChatMessage chatMessage) {
        LOGGER.info("Alert user online by simpMessageSendingOperations:" + chatMessage.toString());
        simpMessageSendingOperations.convertAndSend("/topic/public", chatMessage);
    }
}
