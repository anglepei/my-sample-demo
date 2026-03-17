package com.traespace.filemanager.dto.response.auth;

import com.traespace.filemanager.enums.UserRole;

/**
 * 用户登录响应
 *
 * @author Traespace
 * @since 2024-03-17
 */
public class LoginResponse {

    /**
     * JWT令牌
     */
    private String token;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 角色
     */
    private UserRole role;

    public LoginResponse() {
    }

    public LoginResponse(String token, Long userId, String username, UserRole role) {
        this.token = token;
        this.userId = userId;
        this.username = username;
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public LoginResponse setToken(String token) {
        this.token = token;
        return this;
    }

    public Long getUserId() {
        return userId;
    }

    public LoginResponse setUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public LoginResponse setUsername(String username) {
        this.username = username;
        return this;
    }

    public UserRole getRole() {
        return role;
    }

    public LoginResponse setRole(UserRole role) {
        this.role = role;
        return this;
    }
}
