package com.example.hibernatevalidator.config;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import java.util.Set;

/**
 * Created by dengzhiming on 2019/6/29
 */
@RestControllerAdvice
@Order(value = Ordered.HIGHEST_PRECEDENCE)
public class GlobalExceptionHandler {
    //统一处理请求参数校验(只适用于普通传参)
    @ExceptionHandler({ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleConstraintViolationException(ConstraintViolationException e) {
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        for (ConstraintViolation<?> violation : violations) {
            Path path = violation.getPropertyPath();
            System.out.println(path.toString());
        }
        return e.getMessage();
    }

    @ExceptionHandler({BindException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleBindException(BindException e) {
        StringBuilder stringBuilder = new StringBuilder();
        e.getFieldErrors().forEach(item ->{
            // 获取错误的属性的名字
            String field = item.getField();
            // 获取到错误提示
            String message = item.getDefaultMessage();
            stringBuilder.append(field).append("-").append(message).append(", ");
        });
        return stringBuilder.toString();
    }

}
