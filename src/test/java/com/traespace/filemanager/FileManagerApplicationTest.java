package com.traespace.filemanager;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 主应用类测试
 * 验证Spring Boot应用能够正常启动
 */
@SpringBootTest
@ActiveProfiles("test")
class FileManagerApplicationTest {

    @Test
    void contextLoads() {
        // 验证Spring上下文能够正常加载
        assertThat(true).isTrue();
    }
}
