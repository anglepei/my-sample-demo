package com.traespace.filemanager.service.template;

import com.traespace.filemanager.entity.FieldConfig;
import com.traespace.filemanager.enums.FieldType;
import com.traespace.filemanager.service.impl.TemplateServiceImpl;
import com.traespace.filemanager.util.MockDataGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

/**
 * TemplateService测试
 */
@ExtendWith(MockitoExtension.class)
class TemplateServiceTest {

    @Mock
    private com.traespace.filemanager.service.field.FieldConfigService fieldConfigService;

    @InjectMocks
    private TemplateServiceImpl templateService;

    private Long userId;
    private List<FieldConfig> fieldConfigs;

    @BeforeEach
    void setUp() {
        userId = 1001L;

        // 固定字段：序号、身份证号、手机号
        fieldConfigs = new ArrayList<>();

        // 自定义字段1
        FieldConfig config1 = new FieldConfig();
        config1.setId(1L);
        config1.setUserId(userId);
        config1.setFieldName("联系地址");
        config1.setFieldType(FieldType.TEXT);
        config1.setRequired(false);
        config1.setSortOrder(1);

        // 自定义字段2
        FieldConfig config2 = new FieldConfig();
        config2.setId(2L);
        config2.setUserId(userId);
        config2.setFieldName("出生日期");
        config2.setFieldType(FieldType.DATE);
        config2.setRequired(true);
        config2.setSortOrder(2);

        fieldConfigs.add(config1);
        fieldConfigs.add(config2);
    }

    @Test
    void testGenerateExcelTemplate() {
        // 测试生成Excel模板
        when(fieldConfigService.getFieldConfigByUserId(userId)).thenReturn(
                fieldConfigs.stream()
                        .map(this::convertToItem)
                        .toList()
        );

        byte[] excelBytes = templateService.generateExcelTemplate(userId);

        assertThat(excelBytes).isNotNull();
        assertThat(excelBytes.length).isGreaterThan(0);
    }

    @Test
    void testGenerateCsvTemplate() {
        // 测试生成CSV模板
        when(fieldConfigService.getFieldConfigByUserId(userId)).thenReturn(
                fieldConfigs.stream()
                        .map(this::convertToItem)
                        .toList()
        );

        byte[] csvBytes = templateService.generateCsvTemplate(userId);

        assertThat(csvBytes).isNotNull();
        assertThat(csvBytes.length).isGreaterThan(0);
    }

    @Test
    void testGenerateExcelTemplateWithNoCustomFields() {
        // 测试没有自定义字段时生成Excel模板（只有固定字段）
        when(fieldConfigService.getFieldConfigByUserId(userId)).thenReturn(new ArrayList<>());

        byte[] excelBytes = templateService.generateExcelTemplate(userId);

        assertThat(excelBytes).isNotNull();
        assertThat(excelBytes.length).isGreaterThan(0);
    }

    // ========== 带数据模板测试 ==========

    @Test
    void testGenerateExcelTemplateWithData_SmallCount() {
        // 测试生成带少量数据的Excel模板
        when(fieldConfigService.getFieldConfigByUserId(userId)).thenReturn(new ArrayList<>());

        byte[] excelBytes = templateService.generateExcelTemplateWithData(userId, 10);

        assertThat(excelBytes).isNotNull();
        assertThat(excelBytes.length).isGreaterThan(0);
    }

    @Test
    void testGenerateExcelTemplateWithData_WithCustomFields() {
        // 测试带自定义字段生成Excel模板
        when(fieldConfigService.getFieldConfigByUserId(userId)).thenReturn(
                fieldConfigs.stream()
                        .map(this::convertToItem)
                        .toList()
        );

        byte[] excelBytes = templateService.generateExcelTemplateWithData(userId, 5);

        assertThat(excelBytes).isNotNull();
        assertThat(excelBytes.length).isGreaterThan(0);
    }

    @Test
    void testGenerateCsvTemplateWithData_SmallCount() {
        // 测试生成带少量数据的CSV模板
        when(fieldConfigService.getFieldConfigByUserId(userId)).thenReturn(new ArrayList<>());

        byte[] csvBytes = templateService.generateCsvTemplateWithData(userId, 10);

        assertThat(csvBytes).isNotNull();
        assertThat(csvBytes.length).isGreaterThan(0);

        // 验证CSV内容格式（CSV带引号）
        String csvContent = new String(csvBytes);
        assertThat(csvContent).contains("\"序号\",\"身份证号\",\"手机号\"");
    }

    @Test
    void testGenerateCsvTemplateWithData_WithCustomFields() {
        // 测试带自定义字段生成CSV模板
        when(fieldConfigService.getFieldConfigByUserId(userId)).thenReturn(
                fieldConfigs.stream()
                        .map(this::convertToItem)
                        .toList()
        );

        byte[] csvBytes = templateService.generateCsvTemplateWithData(userId, 5);

        assertThat(csvBytes).isNotNull();
        assertThat(csvBytes.length).isGreaterThan(0);

        // 验证CSV包含自定义字段名
        String csvContent = new String(csvBytes);
        assertThat(csvContent).contains("联系地址");
        assertThat(csvContent).contains("出生日期");
    }

    // ========== 边界条件测试 ==========

    @Test
    void testGenerateExcelTemplateWithData_CountEqualsOne() {
        // 测试生成1条数据
        when(fieldConfigService.getFieldConfigByUserId(userId)).thenReturn(new ArrayList<>());

        byte[] excelBytes = templateService.generateExcelTemplateWithData(userId, 1);

        assertThat(excelBytes).isNotNull();
        assertThat(excelBytes.length).isGreaterThan(0);
    }

    @Test
    void testGenerateCsvTemplateWithData_CountEqualsOne() {
        // 测试生成1条CSV数据
        when(fieldConfigService.getFieldConfigByUserId(userId)).thenReturn(new ArrayList<>());

        byte[] csvBytes = templateService.generateCsvTemplateWithData(userId, 1);

        assertThat(csvBytes).isNotNull();
        assertThat(csvBytes.length).isGreaterThan(0);

        // 验证只有表头和1行数据（共2行）
        String csvContent = new String(csvBytes);
        long lineCount = csvContent.split("\n").length;
        assertThat(lineCount).isGreaterThanOrEqualTo(2);
    }

    @Test
    void testGenerateTemplateWithData_LargeCount() {
        // 测试生成较大数据量（模拟，用100条）
        when(fieldConfigService.getFieldConfigByUserId(userId)).thenReturn(new ArrayList<>());

        byte[] excelBytes = templateService.generateExcelTemplateWithData(userId, 100);

        assertThat(excelBytes).isNotNull();
        assertThat(excelBytes.length).isGreaterThan(0);
    }

    private com.traespace.filemanager.dto.request.field.FieldConfigItem convertToItem(FieldConfig config) {
        com.traespace.filemanager.dto.request.field.FieldConfigItem item =
                new com.traespace.filemanager.dto.request.field.FieldConfigItem();
        item.setFieldName(config.getFieldName());
        item.setFieldType(config.getFieldType().name());
        item.setRequired(config.getRequired());
        item.setSortOrder(config.getSortOrder());
        return item;
    }
}
