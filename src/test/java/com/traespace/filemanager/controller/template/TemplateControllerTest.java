package com.traespace.filemanager.controller.template;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * TemplateController测试
 */
class TemplateControllerTest {

    @Test
    void testControllerExists() {
        // 测试控制器类存在
        assertThat(TemplateController.class).isNotNull();
    }
}
