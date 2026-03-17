package com.traespace.filemanager.vo.file;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

/**
 * DataDetailItemVO测试
 */
class DataDetailItemVOTest {

    @Test
    void testDataDetailItemVO() {
        // 测试数据详情项VO
        DataDetailItemVO vo = new DataDetailItemVO();
        vo.setSeqNo("001");
        vo.setIdCard("110101199001011234");
        vo.setPhone("13800138000");

        Map<String, String> customFields = new HashMap<>();
        customFields.put("联系地址", "北京市");
        vo.setCustomFields(customFields);

        assertThat(vo.getSeqNo()).isEqualTo("001");
        assertThat(vo.getIdCard()).isEqualTo("110101199001011234");
        assertThat(vo.getPhone()).isEqualTo("13800138000");
        assertThat(vo.getCustomFields()).containsEntry("联系地址", "北京市");
    }

    @Test
    void testConstructor() {
        // 测试构造函数
        Map<String, String> customFields = new HashMap<>();
        customFields.put("备注", "测试");

        DataDetailItemVO vo = new DataDetailItemVO("002", "110101199001011235",
                "13900139000", customFields);

        assertThat(vo.getSeqNo()).isEqualTo("002");
        assertThat(vo.getCustomFields()).containsEntry("备注", "测试");
    }

    @Test
    void testEmptyCustomFields() {
        // 测试空自定义字段
        DataDetailItemVO vo = new DataDetailItemVO();
        vo.setSeqNo("003");
        vo.setIdCard("110101199001011236");
        vo.setPhone("13700137000");
        vo.setCustomFields(new HashMap<>());

        assertThat(vo.getCustomFields()).isEmpty();
    }
}
