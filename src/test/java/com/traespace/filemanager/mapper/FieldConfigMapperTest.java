package com.traespace.filemanager.mapper;

import com.traespace.filemanager.entity.FieldConfig;
import com.traespace.filemanager.enums.FieldType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * FieldConfigMapper测试
 */
class FieldConfigMapperTest {

    @Test
    void testFieldConfigMapperInterfaceExists() {
        // 测试Mapper接口存在
        assertThat(FieldConfigMapper.class).isNotNull();
    }

    @Test
    void testFieldConfigEntityExists() {
        // 测试FieldConfig实体存在
        FieldConfig config = new FieldConfig();
        assertThat(config).isNotNull();
    }

    @Test
    void testFieldConfigProperties() {
        // 测试FieldConfig属性设置
        FieldConfig config = new FieldConfig();
        config.setId(1L);
        config.setUserId(1001L);
        config.setFieldName("test_field");
        config.setFieldType(FieldType.TEXT);
        config.setRequired(true);
        config.setSortOrder(1);

        assertThat(config.getId()).isEqualTo(1L);
        assertThat(config.getUserId()).isEqualTo(1001L);
        assertThat(config.getFieldName()).isEqualTo("test_field");
        assertThat(config.getFieldType()).isEqualTo(FieldType.TEXT);
        assertThat(config.getRequired()).isTrue();
        assertThat(config.getSortOrder()).isEqualTo(1);
    }
}
