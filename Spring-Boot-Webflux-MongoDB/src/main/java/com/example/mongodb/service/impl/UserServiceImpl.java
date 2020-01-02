package com.example.mongodb.service.impl;

import com.example.mongodb.entity.User;
import com.example.mongodb.mapper.UserMapper;
import com.example.mongodb.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Created by dengzhiming on 2019/7/10
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private ReactiveMongoTemplate template;
    @Autowired
    private UserMapper mapper;

    @Override
    public Flux<User> selectAll() {
        return this.mapper.findAll();
    }

    @Override
    public Mono<User> selectById(String id) {
        return this.mapper.findById(id);
    }

    @Override
    public Mono<User> create(User user) {
        return this.mapper.save(user);
    }

    @Override
    public Mono<User> updateById(String id, User user) {
        return this.mapper.findById(id)
                .flatMap(
                        u -> {
                            u.setName(user.getName());
                            u.setAge(user.getAge());
                            u.setDescription(user.getDescription());
                            return this.mapper.save(user);
                        }
                );
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return this.mapper.findById(id).flatMap(user -> this.mapper.delete(user));
    }
}
