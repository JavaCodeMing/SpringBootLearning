package com.example.smartdoc.controller;

import com.example.smartdoc.model.User;
import com.power.common.enums.HttpCodeEnum;
import com.power.common.model.CommonResult;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户基本信息操作API
 *
 * @author dengzhiming
 * @version v1.0
 * @date 2020/01/09 10:20
 */
@RestController
@RequestMapping(value = "/user", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

    /**
     * 获取所有用户
     *
     * @return 所有用户信息列表
     * @author dengzhiming
     * @apiNote 测试获取所有用户的GET接口
     */
    @GetMapping("/getAllUser")
    public List<User> getAll() {
        return new ArrayList<>();
    }

    /**
     * 根据id获取用户
     *
     * @param id 用户id
     * @return 用户信息
     * @apiNote 测试根据用户id获取用户信息的GET接口
     */
    @GetMapping("/getUserById")
    public User getOne(Long id) {
        return new User();
    }

    /**
     * 根据姓名和性别获取用户信息
     *
     * @param userName 用户名
     * @param userSex  用户性别
     * @return 用户信息
     * @apiNote 测试根据name和sex获取用户的POST接口
     */
    @PostMapping("/getUserByNameAndSex")
    public User getUserByNameAndSex(String userName, String userSex) {
        return new User();
    }

    /**
     * 新增用户
     *
     * @param body 包含用户信息的JSON字符串
     * @return 响应信息字符串
     * @apiNote 传入JSON字符串进行新增用户的POST接口
     */
    @PostMapping("/insertUser")
    public CommonResult<Object> insertUser(@RequestBody String body) {
        return CommonResult.ok(HttpCodeEnum.SUCCESS).setResult("success");
    }

    /**
     * 修改用户
     *
     * @param body 包含用户信息的JSON字符串
     * @return 响应信息字符串
     * @apiNote 传入JSON字符串进行修改用户的POST接口
     */
    @PostMapping("/updateUser")
    public CommonResult<Object> updateUser(@RequestBody String body) {
        return CommonResult.ok(HttpCodeEnum.SUCCESS).setResult("success");
    }

    /**
     * 删除用户
     *
     * @param id 用户id
     * @return 响应信息字符串
     * @apiNote 根据用户id进行删除用户的POST接口
     */
    @PostMapping("/deleteUser")
    public CommonResult<Object> deleteUser(Long id) {
        return CommonResult.ok(HttpCodeEnum.SUCCESS).setResult("success");
    }
}
