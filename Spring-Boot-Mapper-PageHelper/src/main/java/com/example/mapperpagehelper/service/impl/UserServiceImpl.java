package com.example.mapperpagehelper.service.impl;

import com.example.mapperpagehelper.bean.User;
import com.example.mapperpagehelper.service.UserService;
import org.springframework.stereotype.Service;

@Service("userService")
public class UserServiceImpl extends BaseServiceImpl<User> implements UserService {
}