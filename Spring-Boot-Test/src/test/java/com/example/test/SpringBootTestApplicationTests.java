package com.example.test;

import com.example.test.bean.User;
import com.example.test.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringBootTestApplicationTests {

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    UserService userService;
    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Before
    public void setupMockMvc() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }
    @Test
    public void contextLoads() {
    }

    @Test
    public void test1() {
        // 测试service queryUserById
        User user = this.userService.queryUserById("1");
        Assert.assertEquals("用户名为mike", "mike", user.getUsername());
    }

    @Test
    @Transactional  //该事务注解可在方法执行完自动回滚数据
    public void test2() {
        // 测试service add
        User user = new User();
        user.setUsername("JUnit");
        user.setPasswd("123456");
        user.setStatus("1");
        user.setCreateTime(new Date());
        this.userService.add(user);
    }

    @Test
    public void test3() throws Exception {
        // 测试controller queryuser
        mockMvc.perform(MockMvcRequestBuilders.get("/queryuser/1")
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("mike"))
                .andDo(MockMvcResultHandlers.print());
    }

}
