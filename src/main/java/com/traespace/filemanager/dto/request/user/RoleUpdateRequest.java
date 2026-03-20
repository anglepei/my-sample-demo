package com.traespace.filemanager.dto.request.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * 角色更新请求
 *
 * @author Traespace
 * @since 2024-03-18
 */
public class RoleUpdateRequest {

    /**
     * 角色
     */
    @NotBlank(message = "角色不能为空")
    @Pattern(regexp = "^(USER|ADMIN)$", message = "角色必须是USER或ADMIN")
    private String role;

    /**
     * 获取角色
     *
     * @return 角色
     */
    public String getRole() {
        return role;
    }

    /**
     * 设置角色
     *
     * @param role 角色
     */
    public void setRole(String role) {
        this.role = role;
    }
}
