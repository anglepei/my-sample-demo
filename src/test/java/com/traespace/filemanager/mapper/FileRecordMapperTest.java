package com.traespace.filemanager.mapper;

import com.traespace.filemanager.entity.FileRecord;
import com.traespace.filemanager.enums.FileType;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * FileRecordMapper测试
 */
class FileRecordMapperTest {

    @Test
    void testFileRecordMapperInterfaceExists() {
        // 测试Mapper接口存在
        assertThat(FileRecordMapper.class).isNotNull();
    }

    @Test
    void testFileRecordEntityExists() {
        // 测试FileRecord实体存在
        FileRecord record = new FileRecord();
        assertThat(record).isNotNull();
    }

    @Test
    void testFileRecordProperties() {
        // 测试FileRecord属性设置
        FileRecord record = new FileRecord();
        record.setId(1L);
        record.setUserId(1001L);
        record.setFileName("test.xlsx");
        record.setFileType(FileType.XLSX);
        record.setStatus(1);
        record.setUploadTime(LocalDateTime.now());

        assertThat(record.getId()).isEqualTo(1L);
        assertThat(record.getUserId()).isEqualTo(1001L);
        assertThat(record.getFileName()).isEqualTo("test.xlsx");
        assertThat(record.getFileType()).isEqualTo(FileType.XLSX);
        assertThat(record.getStatus()).isEqualTo(1);
        assertThat(record.getUploadTime()).isNotNull();
    }
}
