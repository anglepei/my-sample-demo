package com.traespace.filemanager.dto.request.field;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * FieldConfigRequest测试
 */
class FieldConfigRequestTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void testValidFieldConfigRequest() {
        // 测试有效的字段配置请求
        FieldConfigRequest request = new FieldConfigRequest();
        List<FieldConfigItem> items = new ArrayList<>();

        FieldConfigItem item1 = new FieldConfigItem();
        item1.setFieldName("联系地址");
        item1.setFieldType("TEXT");
        item1.setRequired(false);
        item1.setSortOrder(1);

        items.add(item1);
        request.setFields(items);

        Set<ConstraintViolation<FieldConfigRequest>> violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }

    @Test
    void testEmptyFields() {
        // 测试空字段列表
        FieldConfigRequest request = new FieldConfigRequest();
        request.setFields(new ArrayList<>());

        Set<ConstraintViolation<FieldConfigRequest>> violations = validator.validate(request);
        assertThat(violations).isNotEmpty();
    }

    @Test
    void testNullFields() {
        // 测试null字段列表
        FieldConfigRequest request = new FieldConfigRequest();
        request.setFields(null);

        Set<ConstraintViolation<FieldConfigRequest>> violations = validator.validate(request);
        assertThat(violations).isNotEmpty();
    }

    @Test
    void testFieldCountExceed() {
        // 测试字段数量超限
        FieldConfigRequest request = new FieldConfigRequest();
        List<FieldConfigItem> items = new ArrayList<>();

        for (int i = 0; i < 12; i++) {
            FieldConfigItem item = new FieldConfigItem();
            item.setFieldName("字段" + i);
            item.setFieldType("TEXT");
            item.setRequired(false);
            item.setSortOrder(i);
            items.add(item);
        }

        request.setFields(items);

        Set<ConstraintViolation<FieldConfigRequest>> violations = validator.validate(request);
        assertThat(violations).isNotEmpty();
    }

    @Test
    void testGettersSetters() {
        // 测试getter和setter
        FieldConfigRequest request = new FieldConfigRequest();
        List<FieldConfigItem> items = new ArrayList<>();

        FieldConfigItem item = new FieldConfigItem();
        item.setFieldName("测试字段");
        item.setFieldType("TEXT");
        items.add(item);

        request.setFields(items);

        assertThat(request.getFields()).hasSize(1);
        assertThat(request.getFields().get(0).getFieldName()).isEqualTo("测试字段");
    }
}
