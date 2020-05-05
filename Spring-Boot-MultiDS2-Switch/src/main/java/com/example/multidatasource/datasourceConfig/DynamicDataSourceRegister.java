package com.example.multidatasource.datasourceConfig;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertyName;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * 动态数据源注册类
 * Created by dengzhiming on 2019/3/29
 */
public class DynamicDataSourceRegister implements ImportBeanDefinitionRegistrar, EnvironmentAware {

    //指定默认数据源(springboot2.0默认数据源是hikari如果想使用其他数据源可以自己配置)
    private static final String DATASOURCE_TYPE_DEFAULT = "com.zaxxer.hikari.HikariDataSource";
    //默认数据源
    private DataSource defaultDataSource;
    //用户自定义数据源
    private Map<String, DataSource> slaveDataSources = new HashMap<>();

    @Override
    public void setEnvironment(Environment environment) {
        initDefaultDataSource(environment);
        initslaveDataSources(environment);
    }

    private void initDefaultDataSource(Environment env) {
        // 读取主数据源
        Binder binder = Binder.get(env);
        Map dsMap = binder.bind("spring.datasource.druid.master", HashMap.class).get();
        defaultDataSource = buildDataSource(dsMap);
    }

    private void initslaveDataSources(Environment env) {
        // 读取配置文件获取更多数据源
        Binder binder = Binder.get(env);
        Map map = binder.bind("spring.datasource.druid.slave", HashMap.class).get();
        for (Object o : map.entrySet()) {
            Map.Entry entry = (Map.Entry) o;
            String dsPrefix = (String) entry.getKey();
            Map<String, Object> dsMap = (Map<String, Object>) entry.getValue();
            DataSource ds = buildDataSource(dsMap);
            slaveDataSources.put(dsPrefix, ds);
        }
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry beanDefinitionRegistry) {
        Map<String, DataSource> targetDataSources = new HashMap<>();
        //添加默认数据源
        targetDataSources.put("dataSource", defaultDataSource);
        DynamicDataSourceContextHolder.dataSourceIds.add("dataSource");
        //添加其他数据源
        targetDataSources.putAll(slaveDataSources);
        DynamicDataSourceContextHolder.dataSourceIds.addAll(slaveDataSources.keySet());

        //创建DynamicDataSource
        GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        beanDefinition.setBeanClass(DynamicDataSource.class);
        beanDefinition.setSynthetic(true);
        MutablePropertyValues mpv = beanDefinition.getPropertyValues();
        mpv.addPropertyValue("defaultTargetDataSource", defaultDataSource);
        mpv.addPropertyValue("targetDataSources", targetDataSources);
        //注册 - BeanDefinitionRegistry
        beanDefinitionRegistry.registerBeanDefinition("dataSource", beanDefinition);
        /*for (String key : slaveDataSources.keySet()) {
            GenericBeanDefinition transactionManagerDefinition = new GenericBeanDefinition();
            transactionManagerDefinition.setBeanClass(DataSourceTransactionManager.class);
            transactionManagerDefinition.setSynthetic(true);
            MutablePropertyValues propertyValues = transactionManagerDefinition.getPropertyValues();
            propertyValues.addPropertyValue("dataSource", slaveDataSources.get(key));
            beanDefinitionRegistry.registerBeanDefinition(key + "TransactionManager",transactionManagerDefinition);
        }*/
    }

    private DataSource buildDataSource(Map dataSourceMap) {
        try {
            String type = dataSourceMap.get("type").toString();
            if (StringUtils.isEmpty(type)) {
                type = DATASOURCE_TYPE_DEFAULT;
            }
            Class<? extends DataSource> dataSourceType = (Class<? extends DataSource>) Class.forName(type);
            ConfigurationPropertySource source = new MapConfigurationPropertySource(dataSourceMap);
            Binder binder = new Binder(source);
            //通过类型绑定参数并获得实例对象
            return binder.bind(ConfigurationPropertyName.EMPTY, Bindable.of(dataSourceType)).get();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
