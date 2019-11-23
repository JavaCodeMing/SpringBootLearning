package com.example.xssfilter.filter;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Xss攻击拦截器
 * Created by dengzhiming on 2019/5/25
 */
public class XssFilter implements Filter {

    // 是否过滤富文本内容
    private boolean IS_INCLUDE_RICH_TEXT = false;

    // 不过滤的请求
    private List<String> excludes = new ArrayList<>();

    // 初始化方法仅用来给成员变量赋值
    @Override
    public void init(FilterConfig filterConfig){
        String isIncludeRichText = filterConfig.getInitParameter("isIncludeRichText");
        if (StringUtils.isNotBlank(isIncludeRichText)) {
            IS_INCLUDE_RICH_TEXT = BooleanUtils.toBoolean(isIncludeRichText);
        }
        String temp = filterConfig.getInitParameter("excludes");
        if (StringUtils.isNotBlank(temp)) {
            String[] url = temp.split(",");
            excludes.addAll(Arrays.asList(url));
        }
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        if (handleExcludeURL(request, response)) {
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            XssHttpServletRequestWrapper xssHttpServletRequestWrapper = new XssHttpServletRequestWrapper(request, IS_INCLUDE_RICH_TEXT);
            filterChain.doFilter(xssHttpServletRequestWrapper, servletResponse);
        }
    }

    // 判断请求路径是否包含在无需过滤的几个请求路径里,是则返回true放行,否则需要进行过滤处理
    private boolean handleExcludeURL(HttpServletRequest request, HttpServletResponse response) {
        if (excludes == null || excludes.isEmpty()) {
            return false;
        }
        String url = request.getServletPath();
        for (String pattern : excludes) {
            Pattern compile = Pattern.compile("^" + pattern);
            Matcher matcher = compile.matcher(url);
            if (matcher.find()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void destroy() {

    }
}
