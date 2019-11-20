package com.example.test.service;

import com.example.test.bean.User;

/**
 * Created by dengzhiming on 2019/4/29
 */
public interface UserService {
    int add(User user);

    int update(User user);

    int deleteById(String id);

    User queryUserById(String id);
}
