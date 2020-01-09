package com.example.smartdoc;

import com.power.common.enums.HttpCodeEnum;
import com.power.common.util.DateTimeUtil;
import com.power.doc.builder.ApiDocBuilder;
import com.power.doc.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class SpringBootSmartdocApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    public void testBuilderApiMarkdown() {
        //文档地址: https://gitee.com/sunyurepository/smart-doc/wikis/Home?sort_id=1652800
        ApiConfig config = new ApiConfig();
        config.setServerUrl("http://localhost:8080");
        //设置为严格模式(要求每个Controller暴露的接口写上标准文档注释)
        config.setStrict(true);
        //设置为true时,所有接口将生成到一个Markdown、HHTML或者AsciiDoc中
        config.setAllInOne(true);
        //设置API文档的输出路径
        config.setOutPath(System.getProperty("user.dir") + "/doc/markdown");
        //设置接口包扫描路径过滤(不配置则默认扫描所有接口类)
        //配置多个报名有英文逗号隔开
        config.setPackageFilters("com.example.smartdoc.controller");
        //设置公共请求头(不需要请求头则无需设置)
        config.setRequestHeaders(
                ApiReqHeader.header().setName("access_token").setType("string")
                        .setDesc("Basic auth credentials").setRequired(true).setSince("v1.0.0"),
                ApiReqHeader.header().setName("user_uuid").setType("string").setDesc("User Uuid key")
        );
        //设置错误码列表,遍历自己的错误码设置给Smart-doc即可(HttpCodeEnum:错误码枚举类)
        List<ApiErrorCode> errorCodeList = new ArrayList<>();
        for (HttpCodeEnum codeEnum : HttpCodeEnum.values()) {
            ApiErrorCode errorCode = new ApiErrorCode();
            errorCode.setValue(codeEnum.getCode()).setDesc(codeEnum.getMessage());
            errorCodeList.add(errorCode);
        }
        //不需要显示错误码,则可不设置
        config.setErrorCodes(errorCodeList);
        //1.7.9版本优化了错误码处理,用于下面替代遍历枚举设置错误码
        //不需在文档中展示错误码则可不设置
        config.setErrorCodeDictionaries(
                ApiErrorCodeDictionary.dict()
                        .setEnumClass(HttpCodeEnum.class)
                        .setCodeField("code") //错误码值字段名
                        .setDescField("desc")//错误码描述
        );
        //设置文档变更记录,不需要可不设置
        config.setRevisionLogs(
                RevisionLog.getLog().setRevisionTime("2020/01/09").setAuthor("deng")
                        .setRemarks("test").setStatus("create").setVersion("V1.0")
        );
        long start = System.currentTimeMillis();
        //生成Markdown文件
        ApiDocBuilder.builderControllersApi(config);
        long end = System.currentTimeMillis();
        DateTimeUtil.printRunTime(end, start);
    }
}
