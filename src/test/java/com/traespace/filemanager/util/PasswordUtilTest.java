package com.traespace.filemanager.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * PasswordUtil测试
 */
class PasswordUtilTest {

    @Test
    void testEncodePassword() {
        // 测试加密密码
        String rawPassword = "password123";
        String encodedPassword = PasswordUtil.encode(rawPassword);

        assertThat(encodedPassword).isNotNull();
        assertThat(encodedPassword).isNotEqualTo(rawPassword);
        assertThat(encodedPassword).startsWith("$2a$");
    }

    @Test
    void testEncodeSamePasswordDifferentResults() {
        // 测试相同密码加密结果不同（盐值不同）
        String rawPassword = "password123";

        String encoded1 = PasswordUtil.encode(rawPassword);
        String encoded2 = PasswordUtil.encode(rawPassword);

        assertThat(encoded1).isNotEqualTo(encoded2);
    }

    @Test
    void testMatchesPassword() {
        // 测试密码验证
        String rawPassword = "password123";
        String encodedPassword = PasswordUtil.encode(rawPassword);

        boolean matches = PasswordUtil.matches(rawPassword, encodedPassword);

        assertThat(matches).isTrue();
    }

    @Test
    void testMatchesWrongPassword() {
        // 测试错误密码验证
        String rawPassword = "password123";
        String wrongPassword = "wrongpassword";
        String encodedPassword = PasswordUtil.encode(rawPassword);

        boolean matches = PasswordUtil.matches(wrongPassword, encodedPassword);

        assertThat(matches).isFalse();
    }

    @Test
    void testMatchesEmptyPassword() {
        // 测试空密码
        String rawPassword = "";
        String encodedPassword = PasswordUtil.encode(rawPassword);

        boolean matches = PasswordUtil.matches(rawPassword, encodedPassword);

        assertThat(matches).isTrue();
    }

    @Test
    void testEncodeNullPassword() {
        // 测试null密码加密会抛出异常
        assertThatThrownBy(() -> PasswordUtil.encode(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testMatchesNullAgainstEncoded() {
        // 测试null密码与加密密码匹配会抛出异常
        String encodedPassword = PasswordUtil.encode("test");

        assertThatThrownBy(() -> PasswordUtil.matches(null, encodedPassword))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
