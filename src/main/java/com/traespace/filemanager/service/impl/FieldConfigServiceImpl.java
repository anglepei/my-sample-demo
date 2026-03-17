package com.traespace.filemanager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.traespace.filemanager.dto.request.field.FieldConfigItem;
import com.traespace.filemanager.dto.request.field.FieldConfigRequest;
import com.traespace.filemanager.entity.FieldConfig;
import com.traespace.filemanager.enums.FieldType;
import com.traespace.filemanager.mapper.FieldConfigMapper;
import com.traespace.filemanager.service.field.FieldConfigService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 字段配置服务实现
 *
 * @author Traespace
 * @since 2024-03-17
 */
@Service
public class FieldConfigServiceImpl implements FieldConfigService {

    private final FieldConfigMapper fieldConfigMapper;

    public FieldConfigServiceImpl(FieldConfigMapper fieldConfigMapper) {
        this.fieldConfigMapper = fieldConfigMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveFieldConfig(Long userId, FieldConfigRequest request) {
        // 先删除旧的配置
        LambdaQueryWrapper<FieldConfig> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(FieldConfig::getUserId, userId);
        fieldConfigMapper.delete(deleteWrapper);

        // 插入新配置
        List<FieldConfigItem> items = request.getFields();
        for (int i = 0; i < items.size(); i++) {
            FieldConfigItem item = items.get(i);
            FieldConfig config = new FieldConfig();
            config.setUserId(userId);
            config.setFieldName(item.getFieldName());
            config.setFieldType(FieldType.valueOf(item.getFieldType()));
            config.setRequired(item.getRequired());
            config.setSortOrder(item.getSortOrder() != null ? item.getSortOrder() : i + 1);

            fieldConfigMapper.insert(config);
        }
    }

    @Override
    public List<FieldConfigItem> getFieldConfigByUserId(Long userId) {
        LambdaQueryWrapper<FieldConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FieldConfig::getUserId, userId)
                .orderByAsc(FieldConfig::getSortOrder);

        List<FieldConfig> configs = fieldConfigMapper.selectList(wrapper);

        return configs.stream()
                .map(this::convertToItem)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteFieldConfig(Long userId) {
        LambdaQueryWrapper<FieldConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FieldConfig::getUserId, userId);
        fieldConfigMapper.delete(wrapper);
    }

    /**
     * 转换为FieldConfigItem
     */
    private FieldConfigItem convertToItem(FieldConfig config) {
        FieldConfigItem item = new FieldConfigItem();
        item.setFieldName(config.getFieldName());
        item.setFieldType(config.getFieldType().name());
        item.setRequired(config.getRequired());
        item.setSortOrder(config.getSortOrder());
        return item;
    }
}
