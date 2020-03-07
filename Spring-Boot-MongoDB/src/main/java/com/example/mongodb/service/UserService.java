package com.example.mongodb.service;

import com.example.mongodb.entity.User;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

/**
 * Created by dengzhiming on 2019/7/6
 */
public interface UserService {
    List<User> selectAll();

    Optional<User> selectById(String id);

    User create(User user);

    void updateById(String id, User user);

    void deleteById(String id);

    Page<User> selectByCondition(int page, int size, User user);
}
