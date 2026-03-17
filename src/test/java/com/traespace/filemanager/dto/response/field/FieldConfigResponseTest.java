package com.traespace.filemanager.dto.response.field;

import com.traespace.filemanager.dto.request.field.FieldConfigItem;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

/**
 * FieldConfigResponse测试
 */
class FieldConfigResponseTest {

    @Test
    void testFieldConfigResponse() {
        // 测试字段配置响应
        FieldConfigResponse response = new FieldConfigResponse();
        List<FieldConfigItem> items = new ArrayList<>();

        FieldConfigItem item1 = new FieldConfigItem();
        item1.setFieldName("联系地址");
        item1.setFieldType("TEXT");
        item1.setRequired(false);
        item1.setSortOrder(1);

        items.add(item1);
        response.setFields(items);

        assertThat(response.getFields()).hasSize(1);
        assertThat(response.getFields().get(0).getFieldName()).isEqualTo("联系地址");
    }

    @Test
    void testConstructor() {
        // 测试构造函数
        List<FieldConfigItem> items = new ArrayList<>();

        FieldConfigItem item = new FieldConfigItem();
        item.setFieldName("联系电话");
        item.setFieldType("TEXT");
        item.setRequired(true);
        item.setSortOrder(2);

        items.add(item);

        FieldConfigResponse response = new FieldConfigResponse(items);

        assertThat(response.getFields()).hasSize(1);
        assertThat(response.getFields().get(0).getFieldName()).isEqualTo("联系电话");
    }

    @Test
    void testEmptyFields() {
        // 测试空字段列表
        FieldConfigResponse response = new FieldConfigResponse(new ArrayList<>());

        assertThat(response.getFields()).isEmpty();
    }
}
