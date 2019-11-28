package com.example.mybatisplus;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.example.mybatisplus.entity.User;
import com.example.mybatisplus.mapper.UserMapper;
import com.example.mybatisplus.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringBootMybatisPlusApplicationTests {

    @Autowired
    private UserService service;
    @Autowired
    private UserMapper mapper;
    @Test
    public void contextLoads() {
    }

    @Test
    public void codeGenerator() {
        //代码生成器对象
        AutoGenerator mpg = new AutoGenerator();
        //数据源配置
        DataSourceConfig dataSourceConfig = new DataSourceConfig();
        dataSourceConfig.setDriverName("com.mysql.cj.jdbc.Driver");
        dataSourceConfig.setUrl("jdbc:mysql://localhost:3306/test?serverTimezone=GMT%2B8");
        dataSourceConfig.setUsername("root");
        dataSourceConfig.setPassword("root");
        mpg.setDataSource(dataSourceConfig);

        //包名配置
        PackageConfig packageConfig = new PackageConfig();
        packageConfig.setParent("com.example.mybatisplus");
        mpg.setPackageInfo(packageConfig);

        //策略配置
        StrategyConfig strategyConfig = new StrategyConfig();
        strategyConfig.setNaming(NamingStrategy.underline_to_camel);
        strategyConfig.setColumnNaming(NamingStrategy.underline_to_camel);
        strategyConfig.setInclude("user");  //表名,多个以英文逗号分隔
        strategyConfig.setControllerMappingHyphenStyle(true);
        mpg.setStrategy(strategyConfig);

        //全局策略配置
        GlobalConfig globalConfig = new GlobalConfig();
        String projectPath = System.getProperty("user.dir");
        globalConfig.setOutputDir(projectPath + "/src/main/java");
        globalConfig.setOpen(false);
        globalConfig.setAuthor("dengzhiming");
        globalConfig.setServiceName("%sService");
        mpg.setGlobalConfig(globalConfig);

        // 自定义配置
        InjectionConfig cfg = new InjectionConfig() {
            @Override
            public void initMap() {
                // to do nothing
            }
        };
        //如果模板引擎是 velocity
        String templatePath = "/templates/mapper.xml.vm";
        // 自定义输出配置
        List<FileOutConfig> focList = new ArrayList<>();
        // 自定义配置会被优先输出
        focList.add(new FileOutConfig(templatePath) {
            @Override
            public String outputFile(TableInfo tableInfo) {
                // 自定义输出文件名,如果你 Entity 设置了前后缀、此处注意 xml 的名称会跟着发生变化！！
                return projectPath + "/src/main/resources/mapper/" + tableInfo.getEntityName() + "Mapper" + StringPool.DOT_XML;
            }
        });
        cfg.setFileOutConfigList(focList);
        mpg.setCfg(cfg);

        mpg.execute();
    }

    @Test
    public void testService(){
        //User user = service.getOne(new QueryWrapper<User>().eq("id","1"));
        User user = service.getById("1");
        System.out.println(user);
    }

    @Test
    public void testMapper(){
        User user = mapper.selectOne(new QueryWrapper<User>().eq("id", "1"));
        System.out.println(user);
    }

    @Test
    public void testSql(){
        User user = mapper.findById("2");
        System.out.println(user);
    }
}
