package com.traespace.filemanager.enums;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * UserRole枚举测试
 */
class UserRoleTest {

    @Test
    void testUserRoleValues() {
        assertThat(UserRole.USER.name()).isEqualTo("USER");
        assertThat(UserRole.ADMIN.name()).isEqualTo("ADMIN");
    }

    @Test
    void testUserRoleCount() {
        assertThat(UserRole.values()).hasSize(2);
    }
}
