package com.traespace.filemanager.dto.response.auth;

import com.traespace.filemanager.enums.UserRole;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * LoginResponse测试
 */
class LoginResponseTest {

    @Test
    void testLoginResponse() {
        // 测试登录响应
        LoginResponse response = new LoginResponse();
        response.setToken("test-token");
        response.setUserId(1001L);
        response.setUsername("testuser");
        response.setRole(UserRole.USER);

        assertThat(response.getToken()).isEqualTo("test-token");
        assertThat(response.getUserId()).isEqualTo(1001L);
        assertThat(response.getUsername()).isEqualTo("testuser");
        assertThat(response.getRole()).isEqualTo(UserRole.USER);
    }

    @Test
    void testConstructor() {
        // 测试构造函数
        LoginResponse response = new LoginResponse("test-token", 1001L, "testuser", UserRole.USER);

        assertThat(response.getToken()).isEqualTo("test-token");
        assertThat(response.getUserId()).isEqualTo(1001L);
        assertThat(response.getUsername()).isEqualTo("testuser");
        assertThat(response.getRole()).isEqualTo(UserRole.USER);
    }

    @Test
    void testBuilderPattern() {
        // 测试链式调用
        LoginResponse response = new LoginResponse()
                .setToken("test-token")
                .setUserId(1001L)
                .setUsername("testuser")
                .setRole(UserRole.ADMIN);

        assertThat(response.getToken()).isEqualTo("test-token");
        assertThat(response.getUserId()).isEqualTo(1001L);
        assertThat(response.getRole()).isEqualTo(UserRole.ADMIN);
    }
}
