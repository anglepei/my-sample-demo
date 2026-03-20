package com.traespace.filemanager.dto.response.user;

import com.traespace.filemanager.enums.UserRole;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * RoleResponse测试
 */
class RoleResponseTest {

    @Test
    void testDefaultConstructor() {
        // 测试默认构造函数
        RoleResponse response = new RoleResponse();

        assertThat(response.getRole())
            .as("默认构造函数创建的对象role应为null")
            .isNull();

        assertThat(response.getRoleDesc())
            .as("默认构造函数创建的对象roleDesc应为null")
            .isNull();
    }

    @Test
    void testSettersAndGettersWithUserRole() {
        // 测试setter和getter：设置UserRole枚举
        RoleResponse response = new RoleResponse();
        response.setRole(UserRole.USER);
        response.setRoleDesc("普通用户");

        assertThat(response.getRole())
            .as("getRole应返回设置的UserRole.USER")
            .isEqualTo(UserRole.USER);

        assertThat(response.getRoleDesc())
            .as("getRoleDesc应返回设置的描述")
            .isEqualTo("普通用户");
    }

    @Test
    void testSettersAndGettersWithAdminRole() {
        // 测试setter和getter：设置ADMIN角色
        RoleResponse response = new RoleResponse();
        response.setRole(UserRole.ADMIN);
        response.setRoleDesc("管理员");

        assertThat(response.getRole())
            .as("getRole应返回设置的UserRole.ADMIN")
            .isEqualTo(UserRole.ADMIN);

        assertThat(response.getRoleDesc())
            .as("getRoleDesc应返回设置的描述")
            .isEqualTo("管理员");
    }

    @Test
    void testUserRoleEnumDescription() {
        // 测试UserRole枚举值与描述的对应关系
        assertThat(UserRole.USER.getDescription())
            .as("USER枚举的描述应为'普通用户'")
            .isEqualTo("普通用户");

        assertThat(UserRole.ADMIN.getDescription())
            .as("ADMIN枚举的描述应为'管理员'")
            .isEqualTo("管理员");
    }

    @Test
    void testRoleResponseWithUserEnum() {
        // 测试RoleResponse与UserRole.USER的配合
        RoleResponse response = new RoleResponse();
        response.setRole(UserRole.USER);
        response.setRoleDesc(UserRole.USER.getDescription());

        assertThat(response.getRole()).isEqualTo(UserRole.USER);
        assertThat(response.getRoleDesc()).isEqualTo("普通用户");
    }

    @Test
    void testRoleResponseWithAdminEnum() {
        // 测试RoleResponse与UserRole.ADMIN的配合
        RoleResponse response = new RoleResponse();
        response.setRole(UserRole.ADMIN);
        response.setRoleDesc(UserRole.ADMIN.getDescription());

        assertThat(response.getRole()).isEqualTo(UserRole.ADMIN);
        assertThat(response.getRoleDesc()).isEqualTo("管理员");
    }

    @Test
    void testMultipleRoleUpdates() {
        // 测试多次更新角色
        RoleResponse response = new RoleResponse();

        response.setRole(UserRole.USER);
        response.setRoleDesc("普通用户");
        assertThat(response.getRole()).isEqualTo(UserRole.USER);

        response.setRole(UserRole.ADMIN);
        response.setRoleDesc("管理员");
        assertThat(response.getRole()).isEqualTo(UserRole.ADMIN);
        assertThat(response.getRoleDesc()).isEqualTo("管理员");

        // 验证角色被正确覆盖
        assertThat(response.getRole()).isNotEqualTo(UserRole.USER);
    }
}
