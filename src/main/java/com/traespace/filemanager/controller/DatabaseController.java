package com.traespace.filemanager.controller;

import com.traespace.filemanager.dto.response.common.Result;
import com.traespace.filemanager.service.DatabaseService;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

/**
 * 数据库初始化控制器
 *
 * @author Traespace
 * @since 2024-03-17
 */
@RestController
@RequestMapping("/api/database")
@RequiredArgsConstructor
public class DatabaseController {

    private final DatabaseService databaseService;

    /**
     * 初始化数据库表
     */
    @PostMapping("/init")
    public Result<Void> initDatabase() {
        databaseService.initDatabase();
        return Result.success();
    }

    /**
     * 重置数据库（删除所有表并重新创建）
     */
    @PostMapping("/reset")
    public Result<Void> resetDatabase() {
        databaseService.resetDatabase();
        return Result.success();
    }

    /**
     * 检查数据库状态
     */
    @GetMapping("/status")
    public Result<Boolean> checkDatabase() {
        boolean initialized = databaseService.isInitialized();
        return Result.success(initialized);
    }
}
