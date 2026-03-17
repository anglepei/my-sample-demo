package com.traespace.filemanager.service.auth;

import com.traespace.filemanager.dto.request.auth.LoginRequest;
import com.traespace.filemanager.dto.request.auth.RegisterRequest;
import com.traespace.filemanager.dto.response.auth.LoginResponse;

/**
 * 认证服务接口
 *
 * @author Traespace
 * @since 2024-03-17
 */
public interface AuthService {

    /**
     * 用户注册
     *
     * @param request 注册请求
     * @return 用户ID
     */
    Long register(RegisterRequest request);

    /**
     * 用户登录
     *
     * @param request 登录请求
     * @return 登录响应
     */
    LoginResponse login(LoginRequest request);

    /**
     * 用户登出
     *
     * @param userId 用户ID
     */
    void logout(Long userId);
}
