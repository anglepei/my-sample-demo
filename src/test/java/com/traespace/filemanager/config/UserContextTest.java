package com.traespace.filemanager.config;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * UserContext测试
 */
class UserContextTest {

    @Test
    void testSetAndGetUserId() {
        // 测试设置和获取用户ID
        UserContext.setUserId(1001L);
        assertThat(UserContext.getUserId()).isEqualTo(1001L);
        UserContext.clear();
    }

    @Test
    void testSetAndGetUsername() {
        // 测试设置和获取用户名
        UserContext.setUsername("testuser");
        assertThat(UserContext.getUsername()).isEqualTo("testuser");
        UserContext.clear();
    }

    @Test
    void testSetAndGetRole() {
        // 测试设置和获取角色
        UserContext.setRole("ADMIN");
        assertThat(UserContext.getRole()).isEqualTo("ADMIN");
        UserContext.clear();
    }

    @Test
    void testClear() {
        // 测试清除上下文
        UserContext.setUserId(1001L);
        UserContext.setUsername("testuser");
        UserContext.setRole("ADMIN");

        UserContext.clear();

        assertThat(UserContext.getUserId()).isNull();
        assertThat(UserContext.getUsername()).isNull();
        assertThat(UserContext.getRole()).isNull();
    }

    @Test
    void testThreadIsolation() throws InterruptedException {
        // 测试线程隔离
        UserContext.setUserId(1001L);

        Thread thread = new Thread(() -> {
            UserContext.setUserId(2002L);
            assertThat(UserContext.getUserId()).isEqualTo(2002L);
            UserContext.clear();
        });

        thread.start();
        thread.join();

        // 主线程的值应该不受影响
        assertThat(UserContext.getUserId()).isEqualTo(1001L);
        UserContext.clear();
    }
}
