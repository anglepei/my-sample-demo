package com.traespace.filemanager.service.impl;

import com.traespace.filemanager.dto.request.field.FieldConfigItem;
import com.traespace.filemanager.service.field.FieldConfigService;
import com.traespace.filemanager.service.template.TemplateService;
import com.traespace.filemanager.util.CsvUtil;
import com.traespace.filemanager.util.ExcelUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 模板服务实现
 *
 * @author Traespace
 * @since 2024-03-17
 */
@Service
public class TemplateServiceImpl implements TemplateService {

    private final FieldConfigService fieldConfigService;

    /**
     * 固定字段表头
     */
    private static final List<String> FIXED_HEADERS = Arrays.asList("序号", "身份证号", "手机号");

    public TemplateServiceImpl(FieldConfigService fieldConfigService) {
        this.fieldConfigService = fieldConfigService;
    }

    @Override
    public byte[] generateExcelTemplate(Long userId) {
        // 获取用户自定义字段
        List<FieldConfigItem> customFields = fieldConfigService.getFieldConfigByUserId(userId);

        // 构建完整表头
        List<String> headers = new ArrayList<>(FIXED_HEADERS);
        for (FieldConfigItem field : customFields) {
            headers.add(field.getFieldName() + (field.getRequired() ? "(必填)" : ""));
        }

        // 生成空数据的Excel
        List<java.util.Map<String, String>> emptyData = new ArrayList<>();
        return ExcelUtil.generateExcel(emptyData, headers);
    }

    @Override
    public byte[] generateCsvTemplate(Long userId) {
        // 获取用户自定义字段
        List<FieldConfigItem> customFields = fieldConfigService.getFieldConfigByUserId(userId);

        // 构建完整表头
        List<String> headers = new ArrayList<>(FIXED_HEADERS);
        for (FieldConfigItem field : customFields) {
            headers.add(field.getFieldName() + (field.getRequired() ? "(必填)" : ""));
        }

        // 生成空数据的CSV
        List<java.util.Map<String, String>> emptyData = new ArrayList<>();
        return CsvUtil.generateCsv(emptyData, headers);
    }
}
