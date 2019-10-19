package com.example.multidatasource.datasourceConfig;

import java.lang.annotation.*;

/**
 * 指定数据源注解类
 * Created by dengzhiming on 2019/3/29
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TargetDataSource {
    String value();
}
