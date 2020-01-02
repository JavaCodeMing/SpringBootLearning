package com.example.mongodb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@EnableReactiveMongoRepositories
@SpringBootApplication
public class SpringBootMongoDbWebfluxApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootMongoDbWebfluxApplication.class, args);
    }

}
