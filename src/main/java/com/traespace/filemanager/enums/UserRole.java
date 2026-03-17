package com.traespace.filemanager.enums;

/**
 * 用户角色枚举
 *
 * @author Traespace
 * @since 2024-03-17
 */
public enum UserRole {

    /**
     * 普通用户
     */
    USER("普通用户"),

    /**
     * 管理员
     */
    ADMIN("管理员");

    /**
     * 角色描述
     */
    private final String description;

    UserRole(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
