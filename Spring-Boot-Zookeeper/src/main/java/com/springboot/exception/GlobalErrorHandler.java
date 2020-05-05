package com.springboot.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author dengzhiming
 * @date 2020/5/5 17:54
 */
@ControllerAdvice
public class GlobalErrorHandler {
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public String exceptionHandler(Throwable error){
        return error.getMessage();
    }
}
