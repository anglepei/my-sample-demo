package com.traespace.filemanager.config;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * WebConfig测试
 */
class WebConfigTest {

    @Test
    void testConfigClassExists() {
        // 测试配置类存在
        assertThat(WebConfig.class).isNotNull();
    }
}
