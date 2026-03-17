package com.traespace.filemanager.dto.request.auth;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

/**
 * RegisterRequest测试
 */
class RegisterRequestTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void testValidRegisterRequest() {
        // 测试有效的注册请求
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setPassword("Password123");
        request.setRole("USER");

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }

    @Test
    void testBlankUsername() {
        // 测试空用户名
        RegisterRequest request = new RegisterRequest();
        request.setUsername("");
        request.setPassword("Password123");
        request.setRole("USER");

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        assertThat(violations).isNotEmpty();
    }

    @Test
    void testShortPassword() {
        // 测试短密码
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setPassword("12345");
        request.setRole("USER");

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        assertThat(violations).isNotEmpty();
    }

    @Test
    void testInvalidRole() {
        // 测试无效角色
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setPassword("Password123");
        request.setRole("INVALID");

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        assertThat(violations).isNotEmpty();
    }

    @Test
    void testGettersSetters() {
        // 测试getter和setter
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setPassword("Password123");
        request.setRole("ADMIN");

        assertThat(request.getUsername()).isEqualTo("testuser");
        assertThat(request.getPassword()).isEqualTo("Password123");
        assertThat(request.getRole()).isEqualTo("ADMIN");
    }
}
