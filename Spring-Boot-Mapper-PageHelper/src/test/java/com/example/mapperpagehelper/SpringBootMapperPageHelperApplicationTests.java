package com.example.mapperpagehelper;

import com.example.mapperpagehelper.bean.User;
import com.example.mapperpagehelper.service.UserService;
import com.github.pagehelper.PageHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringBootMapperPageHelperApplicationTests {

    @Autowired
    private UserService userService;

    @Test
    public void contextLoads() {
    }

    @Test
    public void testJavaGenerator() {
        /*List<String> warmings = new ArrayList<>();
        boolean overwrite = true;
        ConfigurationParser cp = new ConfigurationParser(warmings);
        Configuration config = cp.parseConfiguration(getResourceAsStream("generatorConfig.xml"));
        DefaultShellCallback dsc = new DefaultShellCallback(overwrite);
        MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config,dsc,warmings);
        myBatisGenerator.generate(null);
        for (String warming : warmings) {
            System.out.println(warming);
        }*/
    }

    @Test
    public void testInsert() {
        User user = new User();
        user.setUsername("mike");
        user.setPasswd("ac089b11709f9b9e9980e7c497268dfa");
        user.setCreateTime(new Date());
        user.setStatus("0");
        this.userService.save(user);
    }

    @Test
    public void testQuery() {
        Example example = new Example(User.class);
        example.createCriteria().andCondition("username like '%i%'");
        example.setOrderByClause("id desc");
        List<User> list = this.userService.selectByExample(example);
        for (User user : list) {
            System.out.println(user.getUsername());
        }
        System.out.println("-------");
        List<User> all = this.userService.selectAll();
        for (User user : all) {
            System.out.println(user.getUsername());
        }
        System.out.println("-------");
        User u = new User();
        u.setId(3);
        User user = this.userService.selectByKey(u);
        System.out.println(user.getUsername());
    }

    @Test
    public void testDelete() {
        User user = new User();
        user.setId(4);
        this.userService.delete(user);
    }

    @Test
    public void testPage() {
        PageHelper.startPage(1,2);
        List<User> users = this.userService.selectAll();
        for (User user : users) {
            System.out.println(user.getUsername());
        }
    }
}
