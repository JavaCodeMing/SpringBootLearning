package com.example.elasticsearch.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author dengzhiming
 * @date 2020/5/1 14:42
 */
@Slf4j
@Controller
public class IndexController {
    /**
     * 通用页面跳转
     *
     * @param url
     * @return java.lang.String
     */
    @RequestMapping("{url}.shtml")
    public String page(@PathVariable("url") String url) {
        log.info("url: " + url);
        return url;
    }

    /**
     * 通用页面跳转(二级目录)
     *
     * @param module
     * @param url
     * @return java.lang.String
     */
    @RequestMapping("{module}/{url}.shtml")
    public String page(@PathVariable("module") String module, @PathVariable("url") String url) {
        log.info("url: " + module + "/" + url);
        return module + "/" + url;
    }

    /**
     * 通用页面跳转(三级目录)
     *
     * @param module
     * @param url
     * @return java.lang.String
     */
    @RequestMapping("{module}/{module2}/{url}.shtml")
    public String page(@PathVariable("module") String module, @PathVariable("module2") String module2,
                       @PathVariable("url") String url) {
        log.info("url: " + module + "/" + module2 + "/" + url);
        return module + "/" + module2 + "/" + url;
    }
}
