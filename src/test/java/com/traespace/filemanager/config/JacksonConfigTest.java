package com.traespace.filemanager.config;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * JacksonConfig测试
 */
class JacksonConfigTest {

    @Test
    void testConfigExists() {
        // 测试配置类存在
        JacksonConfig config = new JacksonConfig();
        assertThat(config).isNotNull();
    }
}
