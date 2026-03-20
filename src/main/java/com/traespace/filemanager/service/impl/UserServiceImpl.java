package com.traespace.filemanager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.traespace.filemanager.dto.response.user.RoleResponse;
import com.traespace.filemanager.entity.User;
import com.traespace.filemanager.enums.ErrorCode;
import com.traespace.filemanager.enums.UserRole;
import com.traespace.filemanager.exception.BizException;
import com.traespace.filemanager.mapper.UserMapper;
import com.traespace.filemanager.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 用户服务实现
 *
 * @author Traespace
 * @since 2024-03-17
 */
@Slf4j
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

    @Override
    public RoleResponse getCurrentRole(Long userId) {
        log.info("[用户] 获取当前用户角色, userId={}", userId);

        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BizException(ErrorCode.USER_NOT_FOUND);
        }

        RoleResponse response = new RoleResponse();
        response.setRole(user.getRole());
        response.setRoleDesc(user.getRole().getDescription());

        log.info("[用户] 获取当前用户角色成功, userId={}, role={}", userId, user.getRole());
        return response;
    }

    @Override
    public void updateRole(Long userId, UserRole role) {
        log.info("[用户] 更新用户角色, userId={}, newRole={}", userId, role);

        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BizException(ErrorCode.USER_NOT_FOUND);
        }

        user.setRole(role);
        userMapper.updateById(user);

        log.info("[用户] 更新用户角色成功, userId={}, newRole={}", userId, role);
    }
}
