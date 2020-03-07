package com.example.mongodb.controller;

import com.example.mongodb.entity.User;
import com.example.mongodb.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by dengzhiming on 2019/7/6
 */
@RestController
public class UserController {
    @Autowired
    private UserService service;

    @GetMapping("/user")
    public List<User> getUsers() {
        return this.service.selectAll();
    }

    @GetMapping("/user/{id}")
    public User getUser(@PathVariable String id) {
        return this.service.selectById(id).orElse(null);
    }

    @PostMapping("/user")
    public User createUser(User user) {
        return this.service.create(user);
    }

    @DeleteMapping("/user/{id}")
    public void deleteUser(@PathVariable String id) {
        this.service.deleteById(id);
    }

    @PutMapping("/user/{id}")
    public void updateUser(@PathVariable String id, User user) {
        this.service.updateById(id, user);
    }

    @GetMapping("/user/condition")
    public Page<User> getUserByCondition(int page, int size, User user) {
        return this.service.selectByCondition(page, size, user);
    }
}
