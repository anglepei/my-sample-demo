package com.traespace.filemanager.util;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * JwtUtil测试
 */
class JwtUtilTest {

    @Test
    void testGenerateToken() {
        // 测试生成Token
        Long userId = 1001L;
        String username = "testuser";
        String role = "USER";

        String token = JwtUtil.generateToken(userId, username, role);

        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
        assertThat(token).contains(".");
    }

    @Test
    void testParseToken() {
        // 测试解析Token
        Long userId = 1001L;
        String username = "testuser";
        String role = "ADMIN";

        String token = JwtUtil.generateToken(userId, username, role);

        Long parsedUserId = JwtUtil.getUserId(token);
        String parsedUsername = JwtUtil.getUsername(token);
        String parsedRole = JwtUtil.getRole(token);

        assertThat(parsedUserId).isEqualTo(userId);
        assertThat(parsedUsername).isEqualTo(username);
        assertThat(parsedRole).isEqualTo(role);
    }

    @Test
    void testIsTokenExpired() {
        // 测试Token是否过期
        Long userId = 1001L;
        String username = "testuser";
        String role = "USER";

        String token = JwtUtil.generateToken(userId, username, role);

        // 未过期的Token应该返回false
        assertThat(JwtUtil.isTokenExpired(token)).isFalse();

        // 无效的Token应该返回true
        assertThat(JwtUtil.isTokenExpired("invalid.token")).isTrue();
    }
}
