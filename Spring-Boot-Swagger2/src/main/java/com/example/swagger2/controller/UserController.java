package com.example.swagger2.controller;

import com.example.swagger2.bean.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dengzhiming on 2019/4/29
 */
@Api(tags = "用户Controller")
@Controller
@RequestMapping("user")
public class UserController {

    @ApiIgnore
    public @ResponseBody
    String hello() {
        return "hello";
    }

    @ApiOperation(value = "获取用户信息", notes = "根据用户ID获取用户信息")
    @ApiImplicitParam(name = "id", value = "用户ID", required = true, dataType = "Long", paramType = "path")
    @GetMapping("/query/{id}")
    public @ResponseBody
    User getUserId(@PathVariable("id") Long id) {
        User user = new User();
        user.setId(id);
        user.setName("Conan");
        user.setAge(7);
        return user;
    }

    @ApiOperation(value = "新增用户", notes = "根据用户实体创建用户")
    @ApiImplicitParam(name = "user", value = "用户实体", required = true, dataType = "User")
    @PostMapping("/add")
    public @ResponseBody
    Map<String, Object> addUser(@RequestBody User user) {
        Map<String, Object> map = new HashMap<>();
        map.put("result", "success");
        return map;
    }

    @ApiOperation(value = "更新用户", notes = "根据用户ID更新用户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "用户ID", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "user", value = "用户实体", required = true, dataType = "User")
    })
    @PutMapping("/update/{id}")
    public @ResponseBody
    Map<String, Object> updateUser(@PathVariable("id") String id, @RequestBody User user) {
        Map<String, Object> map = new HashMap<>();
        map.put("result", "success");
        return map;
    }

    @ApiOperation(value = "删除用户", notes = "根据用户ID删除用户")
    @ApiImplicitParam(name = "id", value = "用户ID", required = true, dataType = "Long", paramType = "path")
    @DeleteMapping("/delete/{id}")
    public @ResponseBody
    Map<String, Object> deleteUser(@PathVariable("id") String id) {
        Map<String, Object> map = new HashMap<>();
        map.put("result", "success");
        return map;
    }
}
