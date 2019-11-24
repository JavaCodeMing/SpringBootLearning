package com.example.errorhandler.exception;

/**
 * 主要用途: 存储错误信息的Bean
 * 设计说明:
 * 1.官方提供了默认存储DefaultErrorAttributes,但其返回的存储结构是Map<String,Object>
 * 2.为了避免采用Map这种模糊的数据结构,故统一使用ErrorInfo
 * 3.作为存储Bean,最好不要耦合业务逻辑,故统一使用ErrorInfoBuilder来构建错误信息
 * <p>
 * Created by dengzhiming on 2019/5/27
 */
public class ErrorInfo {

    // 发生时间
    private String time;
    // 访问路径
    private String url;
    // 错误类型
    private String error;
    // 错误的堆栈轨迹
    private String stackTrace;
    // 状态码
    private int statusCode;
    // 状态描述
    private String reasonPhrase;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getReasonPhrase() {
        return reasonPhrase;
    }

    public void setReasonPhrase(String reasonPhrase) {
        this.reasonPhrase = reasonPhrase;
    }
}
