package com.example.filterlistenerinterceptor.filter;

import javax.servlet.*;
import java.io.IOException;

/**
 * Created by dengzhiming on 2019/5/31
 */
//@Component
//@WebFilter(filterName = "customFilter",urlPatterns = "/*")
public class CustomFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) {
        System.out.println("Filter 初始化");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        System.out.println("Filter 开始执行过滤");
        filterChain.doFilter(servletRequest, servletResponse);
        System.out.println("Filter 执行过滤结束");
    }

    @Override
    public void destroy() {
        System.out.println("Filter 销毁");
    }
}
