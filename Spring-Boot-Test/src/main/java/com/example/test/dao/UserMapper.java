package com.example.test.dao;

import com.example.test.bean.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface UserMapper {
    int add(User user);

    int update(User user);

    int deleteById(String id);

    User queryUserById(String id);
}
