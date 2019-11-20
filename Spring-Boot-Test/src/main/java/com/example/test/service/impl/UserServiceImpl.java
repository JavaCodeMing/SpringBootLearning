package com.example.test.service.impl;

import com.example.test.bean.User;
import com.example.test.dao.UserMapper;
import com.example.test.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by dengzhiming on 2019/4/29
 */
@Service("userService")
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    @Autowired
    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public int add(User user) {
        return this.userMapper.add(user);
    }

    @Override
    public int update(User user) {
        return this.userMapper.update(user);
    }

    @Override
    public int deleteById(String id) {
        return this.userMapper.deleteById(id);
    }

    @Override
    public User queryUserById(String id) {
        return this.userMapper.queryUserById(id);
    }
}
