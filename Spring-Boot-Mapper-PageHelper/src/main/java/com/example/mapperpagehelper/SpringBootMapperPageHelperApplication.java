package com.example.mapperpagehelper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

@MapperScan("com.example.mapperpagehelper.mapper")
@SpringBootApplication
public class SpringBootMapperPageHelperApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootMapperPageHelperApplication.class, args);
    }

}
