package com.traespace.filemanager.enums;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * OperationType枚举测试
 */
class OperationTypeTest {

    @Test
    void testOperationTypeValues() {
        assertThat(OperationType.UPLOAD.name()).isEqualTo("UPLOAD");
        assertThat(OperationType.DOWNLOAD.name()).isEqualTo("DOWNLOAD");
        assertThat(OperationType.DELETE.name()).isEqualTo("DELETE");
        assertThat(OperationType.QUERY.name()).isEqualTo("QUERY");
        assertThat(OperationType.OTHER.name()).isEqualTo("OTHER");
    }

    @Test
    void testOperationTypeCount() {
        assertThat(OperationType.values()).hasSize(5);
    }

    @Test
    void testOperationTypeDescriptions() {
        assertThat(OperationType.UPLOAD.getDescription()).isEqualTo("上传");
        assertThat(OperationType.DOWNLOAD.getDescription()).isEqualTo("下载");
        assertThat(OperationType.DELETE.getDescription()).isEqualTo("删除");
        assertThat(OperationType.QUERY.getDescription()).isEqualTo("查询");
        assertThat(OperationType.OTHER.getDescription()).isEqualTo("其他");
    }
}
