package com.example.filterlistenerinterceptor.interceptor;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by dengzhiming on 2019/6/1
 */
public class CustomHandlerInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        System.out.println("HandlerInterceptor 拦截处理前的操作");
        // 返回false,则请求中断
        return true;
    }

    // postHandle只有当被拦截的方法没有抛出异常成功时才会处理
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        System.out.println("HandlerInterceptor 进行拦截处理");
    }

    // afterCompletion方法无论被拦截的方法抛出异常与否都会执行
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        System.out.println("HandlerInterceptor 拦截处理后的操作");
    }
}
