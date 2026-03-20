package com.traespace.filemanager.dto.request.user;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * RoleUpdateRequest测试
 */
class RoleUpdateRequestTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void testValidRoleUser() {
        // 测试有效角色：USER
        RoleUpdateRequest request = new RoleUpdateRequest();
        request.setRole("USER");

        Set<ConstraintViolation<RoleUpdateRequest>> violations = validator.validate(request);

        assertThat(violations)
            .as("角色为USER时应校验通过")
            .isEmpty();
    }

    @Test
    void testValidRoleAdmin() {
        // 测试有效角色：ADMIN
        RoleUpdateRequest request = new RoleUpdateRequest();
        request.setRole("ADMIN");

        Set<ConstraintViolation<RoleUpdateRequest>> violations = validator.validate(request);

        assertThat(violations)
            .as("角色为ADMIN时应校验通过")
            .isEmpty();
    }

    @Test
    void testBlankRole() {
        // 测试空角色
        RoleUpdateRequest request = new RoleUpdateRequest();
        request.setRole("");

        Set<ConstraintViolation<RoleUpdateRequest>> violations = validator.validate(request);

        assertThat(violations)
            .as("角色为空时应校验失败")
            .isNotEmpty();

        assertThat(violations)
            .anyMatch(v -> "role".equals(v.getPropertyPath().toString()));
    }

    @Test
    void testNullRole() {
        // 测试null角色
        RoleUpdateRequest request = new RoleUpdateRequest();
        request.setRole(null);

        Set<ConstraintViolation<RoleUpdateRequest>> violations = validator.validate(request);

        assertThat(violations)
            .as("角色为null时应校验失败")
            .isNotEmpty();
    }

    @Test
    void testInvalidRole() {
        // 测试无效角色：INVALID
        RoleUpdateRequest request = new RoleUpdateRequest();
        request.setRole("INVALID");

        Set<ConstraintViolation<RoleUpdateRequest>> violations = validator.validate(request);

        assertThat(violations)
            .as("角色为INVALID时应校验失败")
            .isNotEmpty();

        assertThat(violations)
            .anyMatch(v -> "role".equals(v.getPropertyPath().toString()));
    }

    @Test
    void testInvalidRoleLowerCase() {
        // 测试小写角色：user（应为大写）
        RoleUpdateRequest request = new RoleUpdateRequest();
        request.setRole("user");

        Set<ConstraintViolation<RoleUpdateRequest>> violations = validator.validate(request);

        assertThat(violations)
            .as("角色为小写user时应校验失败")
            .isNotEmpty();
    }

    @Test
    void testInvalidRoleWithExtraChars() {
        // 测试包含额外字符的角色
        RoleUpdateRequest request = new RoleUpdateRequest();
        request.setRole("USER_ROLE");

        Set<ConstraintViolation<RoleUpdateRequest>> violations = validator.validate(request);

        assertThat(violations)
            .as("角色包含额外字符时应校验失败")
            .isNotEmpty();
    }

    @Test
    void testGettersSetters() {
        // 测试getter和setter
        RoleUpdateRequest request = new RoleUpdateRequest();
        request.setRole("ADMIN");

        assertThat(request.getRole())
            .as("getRole应返回设置的值")
            .isEqualTo("ADMIN");

        request.setRole("USER");
        assertThat(request.getRole())
            .as("getRole应返回更新后的值")
            .isEqualTo("USER");
    }
}
