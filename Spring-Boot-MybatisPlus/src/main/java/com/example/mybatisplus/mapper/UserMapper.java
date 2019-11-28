package com.example.mybatisplus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.mybatisplus.entity.User;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author dengzhiming
 * @since 2019-11-28
 */
@Repository
public interface UserMapper extends BaseMapper<User> {
    User findById(String id);
}
