package com.example.kafka.entity;

import java.io.Serializable;

/**
 * Created by dengzhiming on 2019/7/4
 */
public class Message implements Serializable {
    private static final long serialVersionUID = 6678420965611108427L;
    private String from;
    private String message;

    public Message() {
    }

    public Message(String from, String message) {
        this.from = from;
        this.message = message;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Message{" +
                "from='" + from + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
