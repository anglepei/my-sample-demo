package com.traespace.filemanager.enums;

/**
 * 操作类型枚举
 *
 * @author Traespace
 * @since 2024-03-17
 */
public enum OperationType {

    /**
     * 上传操作
     */
    UPLOAD("上传"),

    /**
     * 下载操作
     */
    DOWNLOAD("下载"),

    /**
     * 删除操作
     */
    DELETE("删除"),

    /**
     * 查询操作
     */
    QUERY("查询"),

    /**
     * 其他操作
     */
    OTHER("其他");

    /**
     * 操作描述
     */
    private final String description;

    OperationType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
