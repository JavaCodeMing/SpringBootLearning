package com.example.errorcontroller.exception;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * 主要用途：统一处理错误/异常(针对控制层)
 *
 * @see BasicErrorController springboot默认的异常控制器
 * Created by dengzhiming on 2019/5/30
 */
@Controller
@RequestMapping("${server.error.path:/error}")
//@RequestMapping({"${server.error.path:${error.path:/error}}"})
public class GlobalErrorController implements ErrorController {
    // 错误信息的构建工具
    private final ErrorInfoBuilder errorInfoBuilder;

    // 错误信息页的路径
    private static final String DEFAULT_ERROR_VIEW = "error";

    @Autowired
    public GlobalErrorController(ErrorInfoBuilder errorInfoBuilder) {
        this.errorInfoBuilder = errorInfoBuilder;
    }

    // 获取错误控制器的映射路径
    @Override
    public String getErrorPath() {
        return errorInfoBuilder.getErrorProperties().getPath();
    }

    // 情况1: 若预期返回类型为text/html,则返回错误信息页(View)
    @RequestMapping(produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public ModelAndView errorHtml(HttpServletRequest request) {
        return new ModelAndView(DEFAULT_ERROR_VIEW, "errorInfo", errorInfoBuilder.getErrorInfo(request));
    }

    // 情况2: 其他类型则返回详细的错误信息(JSON)
    @RequestMapping
    @ResponseBody
    public ErrorInfo error(HttpServletRequest request) {
        return errorInfoBuilder.getErrorInfo(request);
    }
}
