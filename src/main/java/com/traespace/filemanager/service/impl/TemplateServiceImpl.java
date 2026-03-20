package com.traespace.filemanager.service.impl;

import com.traespace.filemanager.dto.request.field.FieldConfigItem;
import com.traespace.filemanager.service.field.FieldConfigService;
import com.traespace.filemanager.service.template.TemplateService;
import com.traespace.filemanager.util.CsvUtil;
import com.traespace.filemanager.util.ExcelUtil;
import com.traespace.filemanager.util.MockDataGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 模板服务实现
 *
 * @author Traespace
 * @since 2024-03-17
 */
@Slf4j
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
        List<Map<String, String>> emptyData = new ArrayList<>();
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
        List<Map<String, String>> emptyData = new ArrayList<>();
        return CsvUtil.generateCsv(emptyData, headers);
    }

    @Override
    public byte[] generateExcelTemplateWithData(Long userId, int count) {
        log.info("[模板生成] 生成带数据Excel模板, userId={}, count={}", userId, count);

        // 1. 获取字段配置
        List<FieldConfigItem> fieldConfigs = fieldConfigService.getFieldConfigByUserId(userId);

        // 2. 构建表头
        List<String> headers = new ArrayList<>(FIXED_HEADERS);
        for (FieldConfigItem config : fieldConfigs) {
            headers.add(config.getFieldName());
        }

        // 3. 构建数据
        List<Map<String, String>> data = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Map<String, String> row = new LinkedHashMap<>();
            row.put("序号", String.valueOf(i + 1));
            row.put("身份证号", MockDataGenerator.generateIdCard());
            row.put("手机号", MockDataGenerator.generatePhone());

            // 添加自定义字段
            for (FieldConfigItem config : fieldConfigs) {
                row.put(config.getFieldName(), generateFieldValue(config));
            }

            data.add(row);

            // 每1000行打印进度日志
            if ((i + 1) % 1000 == 0) {
                log.info("[模板生成] 已生成 {} 行数据", i + 1);
            }
        }

        log.info("[模板生成] Excel模板生成完成, totalRows={}", count);
        return ExcelUtil.generateExcel(data, headers);
    }

    @Override
    public byte[] generateCsvTemplateWithData(Long userId, int count) {
        log.info("[模板生成] 生成带数据CSV模板, userId={}, count={}", userId, count);

        // 1. 获取字段配置
        List<FieldConfigItem> fieldConfigs = fieldConfigService.getFieldConfigByUserId(userId);

        // 2. 构建表头
        List<String> headers = new ArrayList<>(FIXED_HEADERS);
        for (FieldConfigItem config : fieldConfigs) {
            headers.add(config.getFieldName());
        }

        // 3. 构建数据
        List<Map<String, String>> data = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Map<String, String> row = new LinkedHashMap<>();
            row.put("序号", String.valueOf(i + 1));
            row.put("身份证号", MockDataGenerator.generateIdCard());
            row.put("手机号", MockDataGenerator.generatePhone());

            // 添加自定义字段
            for (FieldConfigItem config : fieldConfigs) {
                row.put(config.getFieldName(), generateFieldValue(config));
            }

            data.add(row);

            // 每1000行打印进度日志
            if ((i + 1) % 1000 == 0) {
                log.info("[模板生成] 已生成 {} 行数据", i + 1);
            }
        }

        log.info("[模板生成] CSV模板生成完成, totalRows={}", count);
        return CsvUtil.generateCsv(data, headers);
    }

    /**
     * 根据字段类型生成随机值
     *
     * @param config 字段配置
     * @return 随机值
     */
    private String generateFieldValue(FieldConfigItem config) {
        return switch (config.getFieldType()) {
            case "TEXT" -> MockDataGenerator.generateText(64);
            case "NUMBER" -> MockDataGenerator.generateNumber();
            case "DATE" -> MockDataGenerator.generateDate();
            default -> "";
        };
    }
}
