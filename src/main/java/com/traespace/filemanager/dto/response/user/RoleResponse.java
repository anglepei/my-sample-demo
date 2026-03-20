package com.traespace.filemanager.dto.response.user;

import com.traespace.filemanager.enums.UserRole;

/**
 * 角色响应
 *
 * @author Traespace
 * @since 2024-03-18
 */
public class RoleResponse {

    /**
     * 角色
     */
    private UserRole role;

    /**
     * 角色描述
     */
    private String roleDesc;

    /**
     * 获取角色
     *
     * @return 角色
     */
    public UserRole getRole() {
        return role;
    }

    /**
     * 设置角色
     *
     * @param role 角色
     */
    public void setRole(UserRole role) {
        this.role = role;
    }

    /**
     * 获取角色描述
     *
     * @return 角色描述
     */
    public String getRoleDesc() {
        return roleDesc;
    }

    /**
     * 设置角色描述
     *
     * @param roleDesc 角色描述
     */
    public void setRoleDesc(String roleDesc) {
        this.roleDesc = roleDesc;
    }
}
