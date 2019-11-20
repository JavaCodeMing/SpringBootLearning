package com.example.test.controller;

import com.example.test.bean.User;
import com.example.test.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by dengzhiming on 2019/4/29
 */
@Controller
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(value = "/queryuser/{id}")
    @ResponseBody
    public User queryUserById(@PathVariable("id") String id) {
        return this.userService.queryUserById(id);
    }

    @RequestMapping(value = "/adduser")
    public void addUser(User user) {
        this.userService.add(user);
    }

    @RequestMapping(value = "/updateuser")
    public void updateUser(User user) {
        this.userService.update(user);
    }

    @RequestMapping(value = "/deleteuser")
    public void deleteById(@PathVariable("id") String id) {
        this.userService.deleteById(id);
    }
}
