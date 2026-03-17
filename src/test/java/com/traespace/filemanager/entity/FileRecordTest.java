package com.traespace.filemanager.entity;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

/**
 * FileRecord实体测试
 */
class FileRecordTest {

    @Test
    void testFileRecordProperties() {
        // 测试FileRecord实体属性
        FileRecord record = new FileRecord();
        record.setId(1L);
        record.setFileName("admin_20240317_1.xlsx");
        record.setOriginalName("test_data.xlsx");
        record.setUserId(100L);
        record.setRowCount(100);
        record.setFieldConfigSnapshot("{\"fields\":[]}");
        record.setUploadTime(LocalDateTime.now());

        assertThat(record.getId()).isEqualTo(1L);
        assertThat(record.getFileName()).isEqualTo("admin_20240317_1.xlsx");
        assertThat(record.getOriginalName()).isEqualTo("test_data.xlsx");
        assertThat(record.getUserId()).isEqualTo(100L);
        assertThat(record.getRowCount()).isEqualTo(100);
        assertThat(record.getFieldConfigSnapshot()).isEqualTo("{\"fields\":[]}");
        assertThat(record.getUploadTime()).isNotNull();
    }
}
