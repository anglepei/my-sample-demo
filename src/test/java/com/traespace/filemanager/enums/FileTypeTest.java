package com.traespace.filemanager.enums;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * FileType枚举测试
 */
class FileTypeTest {

    @Test
    void testFileTypeValues() {
        assertThat(FileType.XLSX.name()).isEqualTo("XLSX");
        assertThat(FileType.XLS.name()).isEqualTo("XLS");
        assertThat(FileType.CSV.name()).isEqualTo("CSV");
    }

    @Test
    void testFileTypeCount() {
        assertThat(FileType.values()).hasSize(3);
    }

    @Test
    void testGetExtension() {
        assertThat(FileType.XLSX.getExtension()).isEqualTo(".xlsx");
        assertThat(FileType.XLS.getExtension()).isEqualTo(".xls");
        assertThat(FileType.CSV.getExtension()).isEqualTo(".csv");
    }
}
