package com.traespace.filemanager.entity;

import com.traespace.filemanager.enums.UserRole;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * User实体测试
 */
class UserTest {

    @Test
    void testUserProperties() {
        // 测试User实体属性
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("encrypted_password");
        user.setRole(UserRole.USER);

        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getUsername()).isEqualTo("testuser");
        assertThat(user.getPassword()).isEqualTo("encrypted_password");
        assertThat(user.getRole()).isEqualTo(UserRole.USER);
    }

    @Test
    void testUserEnumValues() {
        // 测试角色枚举
        User admin = new User();
        admin.setRole(UserRole.ADMIN);
        assertThat(admin.getRole()).isEqualTo(UserRole.ADMIN);
        assertThat(admin.getRole().getDescription()).isEqualTo("管理员");
    }
}
