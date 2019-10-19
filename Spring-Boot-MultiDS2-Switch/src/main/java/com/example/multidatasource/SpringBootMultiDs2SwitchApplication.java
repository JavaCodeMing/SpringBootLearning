package com.example.multidatasource;

import com.example.multidatasource.datasourceConfig.DynamicDataSourceRegister;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(DynamicDataSourceRegister.class)
public class SpringBootMultiDs2SwitchApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootMultiDs2SwitchApplication.class, args);
    }

}
