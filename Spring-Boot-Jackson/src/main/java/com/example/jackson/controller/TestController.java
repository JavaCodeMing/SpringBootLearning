package com.example.jackson.controller;

import com.example.jackson.bean.User;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonParseException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * Created by dengzhiming on 2019/4/23
 */
@Controller
public class TestController {

    @Autowired
    ObjectMapper objectMapper;

    @RequestMapping("getuser")
    @ResponseBody
    public User getUser() {
        User user = new User();
        user.setUserName("Mike");
        user.setBirthday(new Date());
        return user;
    }

    @RequestMapping("serialization")
    @ResponseBody
    public String serialization() {
        try {
            User user = new User();
            user.setUserName("Mike");
            user.setBirthday(new Date());
            String str = objectMapper.writeValueAsString(user);
            return str;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @RequestMapping("readjsontostring")
    @ResponseBody
    public String readJsonToString() {
        try {
            String json = "{\"userName\":\"Mike\",\"age\":25,\"hobby\":{\"first\":\"sleep\",\"second\":\"eat\"}}";
            JsonNode jsonNode = this.objectMapper.readTree(json);
            String userName = jsonNode.get("userName").asText();
            String age = jsonNode.get("age").asText();
            JsonNode hobby = jsonNode.get("hobby");
            String first = hobby.get("first").asText();
            String second = hobby.get("second").asText();
            return "userName:" + userName + " age:" + age + " hobby:" + first + "," + second;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @RequestMapping("readjsontoobject")
    @ResponseBody
    public String readJsonToObject() {
        try {
            String json = "{\"userName\":\"Mike\",\"age\":25}";
            User user = objectMapper.readValue(json, User.class);
            String name = user.getUserName();
            int age = user.getAge();
            return name + " " + age;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @RequestMapping("readjsonasobject")
    @ResponseBody
    public String readJsonAsObject() {
        try {
            String json = "{\"user-name\":\"Mike\"}";
            User user = objectMapper.readValue(json, User.class);
            String name = user.getUserName();
            return name;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @JsonView(User.UserBaseView.class)
    @RequestMapping("getView")
    @ResponseBody
    public User getView() {
        User user = new User();
        user.setUserName("Mike");
        user.setAge(25);
        user.setPassword("123456");
        user.setBirthday(new Date());
        return user;
    }

    @RequestMapping("customize")
    @ResponseBody
    public String customize() throws JsonParseException, JsonMappingException, IOException {
        String jsonStr = "[{\"userName\":\"Mike\",\"age\":25},{\"userName\":\"scott\",\"age\":27}]";
        //List<User> list = objectMapper.readValue(jsonStr, List.class);
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(List.class, User.class);
        List<User> list = objectMapper.readValue(jsonStr, javaType);
        String msg = "";
        for (User user : list) {
            msg += user.getUserName();
        }
        return msg;
    }
}
