package com.traespace.filemanager.entity;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * DataDetail实体测试
 */
class DataDetailTest {

    @Test
    void testDataDetailProperties() {
        // 测试DataDetail实体属性
        DataDetail detail = new DataDetail();
        detail.setId(1L);
        detail.setFileId(100L);
        detail.setSeqNo("1");
        detail.setIdCard("110101199001011234");
        detail.setPhone("13800138000");
        detail.setRowNum(2);

        Map<String, String> customFields = new HashMap<>();
        customFields.put("姓名", "张三");
        customFields.put("地址", "北京市朝阳区");
        detail.setCustomFields(customFields);

        assertThat(detail.getId()).isEqualTo(1L);
        assertThat(detail.getFileId()).isEqualTo(100L);
        assertThat(detail.getSeqNo()).isEqualTo("1");
        assertThat(detail.getIdCard()).isEqualTo("110101199001011234");
        assertThat(detail.getPhone()).isEqualTo("13800138000");
        assertThat(detail.getRowNum()).isEqualTo(2);
        assertThat(detail.getCustomFields()).hasSize(2);
        assertThat(detail.getCustomFields().get("姓名")).isEqualTo("张三");
    }
}
