package com.traespace.filemanager.dto.request.field;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

/**
 * FieldConfigItem测试
 */
class FieldConfigItemTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void testValidFieldConfigItem() {
        // 测试有效的字段配置项
        FieldConfigItem item = new FieldConfigItem();
        item.setFieldName("联系地址");
        item.setFieldType("TEXT");
        item.setRequired(false);
        item.setSortOrder(1);

        Set<ConstraintViolation<FieldConfigItem>> violations = validator.validate(item);
        assertThat(violations).isEmpty();
    }

    @Test
    void testBlankFieldName() {
        // 测试空字段名
        FieldConfigItem item = new FieldConfigItem();
        item.setFieldName("");
        item.setFieldType("TEXT");
        item.setRequired(false);

        Set<ConstraintViolation<FieldConfigItem>> violations = validator.validate(item);
        assertThat(violations).isNotEmpty();
    }

    @Test
    void testInvalidFieldType() {
        // 测试无效字段类型
        FieldConfigItem item = new FieldConfigItem();
        item.setFieldName("测试字段");
        item.setFieldType("INVALID");
        item.setRequired(false);

        Set<ConstraintViolation<FieldConfigItem>> violations = validator.validate(item);
        assertThat(violations).isNotEmpty();
    }

    @Test
    void testGettersSetters() {
        // 测试getter和setter
        FieldConfigItem item = new FieldConfigItem();
        item.setFieldName("测试字段");
        item.setFieldType("NUMBER");
        item.setRequired(true);
        item.setSortOrder(10);

        assertThat(item.getFieldName()).isEqualTo("测试字段");
        assertThat(item.getFieldType()).isEqualTo("NUMBER");
        assertThat(item.getRequired()).isTrue();
        assertThat(item.getSortOrder()).isEqualTo(10);
    }
}
