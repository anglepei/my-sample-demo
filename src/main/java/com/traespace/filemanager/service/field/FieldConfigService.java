package com.traespace.filemanager.service.field;

import com.traespace.filemanager.dto.request.field.FieldConfigItem;
import com.traespace.filemanager.dto.request.field.FieldConfigRequest;

import java.util.List;

/**
 * 字段配置服务接口
 *
 * @author Traespace
 * @since 2024-03-17
 */
public interface FieldConfigService {

    /**
     * 保存字段配置（先删除旧的，再插入新的）
     *
     * @param userId  用户ID
     * @param request 字段配置请求
     */
    void saveFieldConfig(Long userId, FieldConfigRequest request);

    /**
     * 获取用户的字段配置
     *
     * @param userId 用户ID
     * @return 字段配置列表
     */
    List<FieldConfigItem> getFieldConfigByUserId(Long userId);

    /**
     * 删除用户的字段配置
     *
     * @param userId 用户ID
     */
    void deleteFieldConfig(Long userId);
}
