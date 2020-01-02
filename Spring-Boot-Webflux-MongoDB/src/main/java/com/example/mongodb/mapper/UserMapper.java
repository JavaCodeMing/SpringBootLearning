package com.example.mongodb.mapper;

import com.example.mongodb.entity.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by dengzhiming on 2019/7/10
 */
@Repository
public interface UserMapper extends ReactiveMongoRepository<User, String> {
}
