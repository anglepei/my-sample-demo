package com.traespace.filemanager.config;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * MyBatisPlusConfig测试
 */
class MyBatisPlusConfigTest {

    @Test
    void testConfigExists() {
        // 测试配置类存在
        MyBatisPlusConfig config = new MyBatisPlusConfig();
        assertThat(config).isNotNull();
    }
}
