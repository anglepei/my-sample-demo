package com.traespace.filemanager.entity;

import com.traespace.filemanager.enums.FieldType;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * FieldConfig实体测试
 */
class FieldConfigTest {

    @Test
    void testFieldConfigProperties() {
        // 测试FieldConfig实体属性
        FieldConfig config = new FieldConfig();
        config.setId(1L);
        config.setUserId(100L);
        config.setFieldName("姓名");
        config.setFieldType(FieldType.TEXT);
        config.setRequired(true);
        config.setSortOrder(1);

        assertThat(config.getId()).isEqualTo(1L);
        assertThat(config.getUserId()).isEqualTo(100L);
        assertThat(config.getFieldName()).isEqualTo("姓名");
        assertThat(config.getFieldType()).isEqualTo(FieldType.TEXT);
        assertThat(config.getRequired()).isTrue();
        assertThat(config.getSortOrder()).isEqualTo(1);
    }

    @Test
    void testFieldTypeMaxLength() {
        // 测试字段类型最大长度
        FieldConfig config = new FieldConfig();
        config.setFieldType(FieldType.TEXT);
        assertThat(config.getFieldType().getMaxLength()).isEqualTo(64);

        config.setFieldType(FieldType.NUMBER);
        assertThat(config.getFieldType().getMaxLength()).isNull();
    }
}
