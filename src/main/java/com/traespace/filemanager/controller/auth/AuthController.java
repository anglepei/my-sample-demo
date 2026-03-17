package com.traespace.filemanager.controller.auth;

import com.traespace.filemanager.dto.request.auth.LoginRequest;
import com.traespace.filemanager.dto.request.auth.RegisterRequest;
import com.traespace.filemanager.dto.response.auth.LoginResponse;
import com.traespace.filemanager.service.auth.AuthService;
import com.traespace.filemanager.dto.response.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 *
 * @author Traespace
 * @since 2024-03-17
 */
@Tag(name = "认证接口", description = "用户注册、登录、登出")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * 用户注册
     */
    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result<Long> register(@Valid @RequestBody RegisterRequest request) {
        Long userId = authService.register(request);
        return Result.success(userId);
    }

    /**
     * 用户登录
     */
    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return Result.success(response);
    }

    /**
     * 用户登出
     */
    @Operation(summary = "用户登出")
    @PostMapping("/logout")
    public Result<Void> logout() {
        // 获取当前用户ID（从拦截器设置的request属性中获取）
        // JWT是无状态的，登出主要由客户端删除Token
        return Result.success();
    }
}
