package com.traespace.filemanager.vo.file;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

/**
 * FileListItemVO测试
 */
class FileListItemVOTest {

    @Test
    void testFileListItemVO() {
        // 测试文件列表项VO
        FileListItemVO vo = new FileListItemVO();
        vo.setId(1L);
        vo.setFileName("test.xlsx");
        vo.setFileType(com.traespace.filemanager.enums.FileType.XLSX);
        vo.setStatus(1);
        vo.setUploadTime(LocalDateTime.now());
        vo.setDataCount(100);

        assertThat(vo.getId()).isEqualTo(1L);
        assertThat(vo.getFileName()).isEqualTo("test.xlsx");
        assertThat(vo.getFileType()).isEqualTo(com.traespace.filemanager.enums.FileType.XLSX);
        assertThat(vo.getStatus()).isEqualTo(1);
        assertThat(vo.getDataCount()).isEqualTo(100);
    }

    @Test
    void testConstructor() {
        // 测试构造函数
        LocalDateTime now = LocalDateTime.now();
        FileListItemVO vo = new FileListItemVO(1L, "data.csv",
                com.traespace.filemanager.enums.FileType.CSV, 1, now, 50);

        assertThat(vo.getId()).isEqualTo(1L);
        assertThat(vo.getFileName()).isEqualTo("data.csv");
    }

    @Test
    void testBuilderPattern() {
        // 测试链式调用
        FileListItemVO vo = new FileListItemVO()
                .setId(1L)
                .setFileName("test.xlsx")
                .setFileType(com.traespace.filemanager.enums.FileType.XLSX)
                .setDataCount(100);

        assertThat(vo.getFileName()).isEqualTo("test.xlsx");
        assertThat(vo.getDataCount()).isEqualTo(100);
    }
}
