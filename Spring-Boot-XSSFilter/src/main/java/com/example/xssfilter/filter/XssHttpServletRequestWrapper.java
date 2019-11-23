package com.example.xssfilter.filter;

import com.example.xssfilter.utils.JsoupUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.stream.Stream;

/**
 * Jsoup过滤http请求，防止Xss攻击
 * Created by dengzhiming on 2019/5/25
 */
public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {

    private HttpServletRequest orgRequest = null;
    private boolean isIncludeRichText = false;

    XssHttpServletRequestWrapper(HttpServletRequest request, boolean isIncludeRichText) {
        super(request);
        orgRequest = request;
        this.isIncludeRichText = isIncludeRichText;
    }

    /**
     * 覆盖getHeader方法,将参数名和参数值都做xss过滤
     * 如果需要获得原始的值,则通过super.getHeaders(name)来获取
     * getHeaderNames 也可能需要覆盖
     */
    @Override
    public String getHeader(String name) {
        name = JsoupUtil.clean(name);
        String value = super.getHeader(name);
        if (StringUtils.isNotBlank(value)) {
            value = JsoupUtil.clean(value);
        }
        return value;
    }

    /**
     * 覆盖getParameter方法，将参数名和参数值都做xss过滤。
     * 如果需要获得原始的值，则通过super.getParameterValues(name)来获取
     * getParameterNames,getParameterValues和getParameterMap也可能需要覆盖
     */
    @Override
    public String getParameter(String name) {
        if (("content".equals(name) || name.endsWith("WithHtml")) && !isIncludeRichText) {
            return super.getParameter(name);
        }
        name = JsoupUtil.clean(name);
        String value = super.getParameter(name);
        if (StringUtils.isNotBlank(value)) {
            value = JsoupUtil.clean(value);
        }
        return value;
    }

    /**
     * 覆盖getParameterValues方法
     * 如果需要获得原始的值，则通过super.getParameterValues(name)来获取
     */
    @Override
    public String[] getParameterValues(String name) {
        String[] values = super.getParameterValues(name);
        if (ArrayUtils.isNotEmpty(values)) {
            values = Stream.of(values).map(JsoupUtil::clean).toArray(String[]::new);
        }
        return values;
    }

    /**
     * 获取最原始的request
     */
    private HttpServletRequest getOrgRequest() {
        return orgRequest;
    }

    /**
     * 获取原始的request的静态方法
     */
    public static HttpServletRequest getOrgRequest(HttpServletRequest request) {
        if (request instanceof XssHttpServletRequestWrapper) {
            return ((XssHttpServletRequestWrapper) request).getOrgRequest();
        }
        return request;
    }
}
