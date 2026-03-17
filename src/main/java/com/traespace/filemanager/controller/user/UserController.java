package com.traespace.filemanager.controller.user;

import com.traespace.filemanager.config.UserContext;
import com.traespace.filemanager.entity.User;
import com.traespace.filemanager.service.user.UserService;
import com.traespace.filemanager.dto.response.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 *
 * @author Traespace
 * @since 2024-03-17
 */
@Tag(name = "用户接口", description = "用户信息管理")
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 获取当前用户信息
     */
    @Operation(summary = "获取当前用户信息")
    @GetMapping("/info")
    public Result<User> getUserInfo() {
        Long userId = UserContext.getUserId();
        User user = userService.getUserById(userId);
        return Result.success(user);
    }

    /**
     * 创建用户（管理员功能）
     */
    @Operation(summary = "创建用户")
    @PostMapping("/create")
    public Result<Long> createUser(@RequestBody User user) {
        Long userId = userService.createUser(user);
        return Result.success(userId);
    }
}
