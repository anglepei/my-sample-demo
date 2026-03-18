package com.traespace.filemanager.controller.log;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.traespace.filemanager.config.UserContext;
import com.traespace.filemanager.dto.request.common.BasePageRequest;
import com.traespace.filemanager.dto.response.common.Result;
import com.traespace.filemanager.service.log.OperationLogService;
import com.traespace.filemanager.vo.log.OperationLogItemVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 操作日志控制器
 *
 * @author Traespace
 * @since 2024-03-17
 */
@Tag(name = "操作日志接口", description = "操作日志查询")
@RestController
@RequestMapping("/api/log")
public class OperationLogController {

    private final OperationLogService operationLogService;

    public OperationLogController(OperationLogService operationLogService) {
        this.operationLogService = operationLogService;
    }

    /**
     * 获取当前用户的操作日志
     */
    @Operation(summary = "获取当前用户的操作日志")
    @GetMapping("/list")
    public Result<IPage<OperationLogItemVO>> getOperationLogs(@Valid BasePageRequest request) {
        Long userId = UserContext.getUserId();
        IPage<OperationLogItemVO> page = operationLogService.getLogPage(userId, request);
        return Result.success(page);
    }

    /**
     * 获取所有用户的操作日志（管理员）
     */
    @Operation(summary = "获取所有用户的操作日志")
    @GetMapping("/all")
    public Result<IPage<OperationLogItemVO>> getAllOperationLogs(@Valid BasePageRequest request) {
        IPage<OperationLogItemVO> page = operationLogService.getLogPage(null, request);
        return Result.success(page);
    }
}
