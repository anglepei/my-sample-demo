package com.traespace.filemanager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.traespace.filemanager.entity.User;
import com.traespace.filemanager.mapper.UserMapper;
import com.traespace.filemanager.service.user.UserService;
import org.springframework.stereotype.Service;

/**
 * 用户服务实现
 *
 * @author Traespace
 * @since 2024-03-17
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public Long createUser(User user) {
        userMapper.insert(user);
        return user.getId();
    }

    @Override
    public User getUserById(Long userId) {
        return userMapper.selectById(userId);
    }

    @Override
    public User getUserByUsername(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        return userMapper.selectOne(wrapper);
    }

    @Override
    public void updateUser(User user) {
        userMapper.updateById(user);
    }
}
