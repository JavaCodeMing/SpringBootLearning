package com.example.thymeleaf.controller;

import com.example.thymeleaf.bean.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dengzhiming on 2019/4/12
 */
@Controller
public class IndexController {
    @RequestMapping("/index")
    public String index(Model model) {
        List<User> list = new ArrayList<>();
        list.add(new User("KangKang", "康康", "e10adc3949ba59abbe56e", "超级管理员", "17611111111"));
        list.add(new User("Mike", "麦克", "e10adc3949ba59abbe56e", "管理员", "15111111111"));
        list.add(new User("Jane", "简", "e10adc3949ba59abbe56e", "运维人员", "18611111111"));
        list.add(new User("Maria", "玛利亚", "e10adc3949ba59abbe56e", "清算人员", "15511111111"));
        model.addAttribute("userList", list);
        return "index";
    }
}
