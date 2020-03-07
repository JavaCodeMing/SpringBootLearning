package com.example.multidatasource.datasourceConfig;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertyName;
import org.springframework.boot.context.properties.source.ConfigurationPropertyNameAliases;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * 动态数据源注册类
 * Created by dengzhiming on 2019/3/29
 */
public class DynamicDataSourceRegister implements ImportBeanDefinitionRegistrar, EnvironmentAware {

    //指定默认数据源(springboot2.0默认数据源是hikari如何想使用其他数据源可以自己配置)
    private static final String DATASOURCE_TYPE_DEFAULT = "com.zaxxer.hikari.HikariDataSource";
    //默认数据源
    private DataSource defaultDataSource;
    //用户自定义数据源
    private Map<String, DataSource> slaveDataSources = new HashMap<>();

    private final static ConfigurationPropertyNameAliases aliases = new ConfigurationPropertyNameAliases(); //别名

    static {
        //由于部分数据源配置不同，所以在此处添加别名，避免切换数据源出现某些参数无法注入的情况
        aliases.addAliases("url", "jdbc-url");
        aliases.addAliases("username", "user");
    }


    @Override
    public void setEnvironment(Environment environment) {
        initDefaultDataSource(environment);
        initslaveDataSources(environment);
    }

    private void initDefaultDataSource(Environment env) {
        // 读取主数据源
        Binder binder = Binder.get(env);
        Map dsMap = binder.bind("spring.datasource.druid.master", Map.class).get();
        defaultDataSource = buildDataSource(dsMap);
    }

    private void initslaveDataSources(Environment env) {
        // 读取配置文件获取更多数据源
        Binder binder = Binder.get(env);
        HashMap map = binder.bind("spring.datasource.druid.slave", HashMap.class).get();
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
        Map<Object, Object> targetDataSources = new HashMap<>();
        //添加默认数据源
        targetDataSources.put("dataSource", this.defaultDataSource);
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
    }

    private DataSource buildDataSource(Map dataSourceMap) {
        try {
            Object type = dataSourceMap.get("type");
            if (type == null) {
                type = DATASOURCE_TYPE_DEFAULT;// 默认DataSource
            }
            Class<? extends DataSource> dataSourceType = (Class<? extends DataSource>) Class.forName((String) type);
            ConfigurationPropertySource source = new MapConfigurationPropertySource(dataSourceMap);
            Binder binder = new Binder(source.withAliases(aliases));
            //通过类型绑定参数并获得实例对象
            return binder.bind(ConfigurationPropertyName.EMPTY, Bindable.of(dataSourceType)).get();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
