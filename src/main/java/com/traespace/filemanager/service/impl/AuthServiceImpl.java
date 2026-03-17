package com.traespace.filemanager.service.impl;

import com.traespace.filemanager.dto.request.auth.LoginRequest;
import com.traespace.filemanager.dto.request.auth.RegisterRequest;
import com.traespace.filemanager.dto.response.auth.LoginResponse;
import com.traespace.filemanager.entity.User;
import com.traespace.filemanager.enums.ErrorCode;
import com.traespace.filemanager.enums.UserRole;
import com.traespace.filemanager.exception.BizException;
import com.traespace.filemanager.mapper.UserMapper;
import com.traespace.filemanager.service.auth.AuthService;
import com.traespace.filemanager.service.user.UserService;
import com.traespace.filemanager.util.JwtUtil;
import com.traespace.filemanager.util.PasswordUtil;
import org.springframework.stereotype.Service;

/**
 * 认证服务实现
 *
 * @author Traespace
 * @since 2024-03-17
 */
@Service
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final UserService userService;

    public AuthServiceImpl(UserMapper userMapper, UserService userService) {
        this.userMapper = userMapper;
        this.userService = userService;
    }

    @Override
    public Long register(RegisterRequest request) {
        // 检查用户名是否已存在
        User existingUser = userMapper.selectByUsername(request.getUsername());
        if (existingUser != null) {
            throw new BizException(ErrorCode.USERNAME_EXISTS);
        }

        // 创建新用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(PasswordUtil.encode(request.getPassword()));
        user.setRole(UserRole.valueOf(request.getRole()));
        user.setStatus(1);

        userMapper.insert(user);
        return user.getId();
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        // 查询用户
        User user = userMapper.selectByUsername(request.getUsername());
        if (user == null) {
            throw new BizException(ErrorCode.USER_NOT_FOUND);
        }

        // 验证密码
        if (!PasswordUtil.matches(request.getPassword(), user.getPassword())) {
            throw new BizException(ErrorCode.PASSWORD_ERROR);
        }

        // 检查用户状态
        if (user.getStatus() != 1) {
            throw new BizException(ErrorCode.USER_DISABLED);
        }

        // 生成Token
        String token = JwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole().name());

        return new LoginResponse(token, user.getId(), user.getUsername(), user.getRole());
    }

    @Override
    public void logout(Long userId) {
        // 登出逻辑（如果使用Redis存储Token，可以在此清除）
        // 当前实现是无状态的JWT，登出主要是客户端删除Token
    }
}
