package com.traespace.filemanager.mapper;

import com.traespace.filemanager.entity.DataDetail;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * DataDetailMapper测试
 */
class DataDetailMapperTest {

    @Test
    void testDataDetailMapperInterfaceExists() {
        // 测试Mapper接口存在
        assertThat(DataDetailMapper.class).isNotNull();
    }

    @Test
    void testDataDetailEntityExists() {
        // 测试DataDetail实体存在
        DataDetail detail = new DataDetail();
        assertThat(detail).isNotNull();
    }

    @Test
    void testDataDetailProperties() {
        // 测试DataDetail属性设置
        DataDetail detail = new DataDetail();
        detail.setId(1L);
        detail.setFileId(100L);
        detail.setSeqNo("001");
        detail.setIdCard("110101199001011234");
        detail.setPhone("13800138000");
        detail.setRowNum(1);

        Map<String, String> customFields = new HashMap<>();
        customFields.put("custom1", "value1");
        detail.setCustomFields(customFields);

        assertThat(detail.getId()).isEqualTo(1L);
        assertThat(detail.getFileId()).isEqualTo(100L);
        assertThat(detail.getSeqNo()).isEqualTo("001");
        assertThat(detail.getIdCard()).isEqualTo("110101199001011234");
        assertThat(detail.getPhone()).isEqualTo("13800138000");
        assertThat(detail.getRowNum()).isEqualTo(1);
        assertThat(detail.getCustomFields()).hasSize(1);
    }
}
