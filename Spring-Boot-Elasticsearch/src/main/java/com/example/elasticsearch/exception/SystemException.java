package com.example.elasticsearch.exception;

public class SystemException extends RuntimeException {
    public SystemException(String msg){
        super(msg);
    }
    public SystemException() {
        super();
    }
}
