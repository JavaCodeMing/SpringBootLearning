package com.example.mongodb.service;

import com.example.mongodb.entity.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Created by dengzhiming on 2019/7/10
 */
public interface UserService {
    // 查询文档内所有数据
    Flux<User> selectAll();

    // 通过id查询文档内的数据
    Mono<User> selectById(String id);

    // 向文档内插入一条记录
    Mono<User> create(User user);

    // 通过id更新文档内容
    Mono<User> updateById(String id, User user);

    // 通过id删除文档内容
    Mono<Void> deleteById(String id);
}
