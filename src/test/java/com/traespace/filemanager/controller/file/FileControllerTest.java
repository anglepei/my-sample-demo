package com.traespace.filemanager.controller.file;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * FileController测试
 */
class FileControllerTest {

    @Test
    void testControllerExists() {
        // 测试控制器类存在
        assertThat(FileController.class).isNotNull();
    }
}
