package com.traespace.filemanager.config;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * RedisConfig测试
 */
class RedisConfigTest {

    @Test
    void testConfigExists() {
        // 测试配置类存在
        RedisConfig config = new RedisConfig();
        assertThat(config).isNotNull();
    }
}
