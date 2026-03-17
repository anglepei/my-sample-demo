package com.traespace.filemanager.controller.field;

import com.traespace.filemanager.config.UserContext;
import com.traespace.filemanager.dto.request.field.FieldConfigRequest;
import com.traespace.filemanager.dto.response.common.Result;
import com.traespace.filemanager.dto.response.field.FieldConfigResponse;
import com.traespace.filemanager.service.field.FieldConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 字段配置控制器
 *
 * @author Traespace
 * @since 2024-03-17
 */
@Tag(name = "字段配置接口", description = "自定义字段配置管理")
@RestController
@RequestMapping("/api/field")
public class FieldConfigController {

    private final FieldConfigService fieldConfigService;

    public FieldConfigController(FieldConfigService fieldConfigService) {
        this.fieldConfigService = fieldConfigService;
    }

    /**
     * 获取当前用户的字段配置
     */
    @Operation(summary = "获取字段配置")
    @GetMapping("/config")
    public Result<FieldConfigResponse> getFieldConfig() {
        Long userId = UserContext.getUserId();
        List<com.traespace.filemanager.dto.request.field.FieldConfigItem> fields =
                fieldConfigService.getFieldConfigByUserId(userId);

        FieldConfigResponse response = new FieldConfigResponse(fields);
        return Result.success(response);
    }

    /**
     * 保存字段配置
     */
    @Operation(summary = "保存字段配置")
    @PostMapping("/config")
    public Result<Void> saveFieldConfig(@Valid @RequestBody FieldConfigRequest request) {
        Long userId = UserContext.getUserId();
        fieldConfigService.saveFieldConfig(userId, request);
        return Result.success();
    }

    /**
     * 删除字段配置
     */
    @Operation(summary = "删除字段配置")
    @DeleteMapping("/config")
    public Result<Void> deleteFieldConfig() {
        Long userId = UserContext.getUserId();
        fieldConfigService.deleteFieldConfig(userId);
        return Result.success();
    }
}
