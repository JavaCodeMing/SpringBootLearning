package com.example.mongodb.service.impl;

import com.example.mongodb.entity.User;
import com.example.mongodb.mapper.UserMapper;
import com.example.mongodb.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

/**
 * Created by dengzhiming on 2019/7/6
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private MongoTemplate template;
    @Autowired
    private UserMapper mapper;

    @Override
    public List<User> selectAll() {
        return this.mapper.findAll();
    }

    @Override
    public Optional<User> selectById(String id) {
        return this.mapper.findById(id);
    }

    @Override
    public User create(User user) {
        user.setId(null);
        return this.mapper.save(user);
    }

    @Override
    public void updateById(String id, User user) {
        this.mapper.findById(id)
                .ifPresent(
                        u -> {
                            u.setName(user.getName());
                            u.setAge(user.getAge());
                            u.setDescription(user.getDescription());
                            this.mapper.save(u);
                        }
                );
    }

    @Override
    public void deleteById(String id) {
        this.mapper.findById(id)
                .ifPresent(user -> this.mapper.deleteById(id));
    }

    @Override
    public Page<User> selectByCondition(int page, int size, User user) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        if (!StringUtils.isEmpty(user.getName())) {
            criteria.and("name").is(user.getName());
        }
        if (!StringUtils.isEmpty(user.getDescription())) {
            criteria.and("description").regex(user.getDescription());
        }
        query.addCriteria(criteria);
        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC,"age"));

        List<User> users = template.find(query.with(pageable), User.class);
        return PageableExecutionUtils.getPage(users, pageable, () -> template.count(query, User.class));
    }
}
