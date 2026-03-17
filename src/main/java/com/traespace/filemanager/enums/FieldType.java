package com.traespace.filemanager.enums;

/**
 * 字段类型枚举
 *
 * @author Traespace
 * @since 2024-03-17
 */
public enum FieldType {

    /**
     * 文本类型（最长64字符）
     */
    TEXT("文本", 64),

    /**
     * 数字类型（整数）
     */
    NUMBER("数字", null),

    /**
     * 日期类型（YYYY-MM-DD）
     */
    DATE("日期", null);

    /**
     * 类型描述
     */
    private final String description;

    /**
     * 最大长度（null表示无限制或不是文本类型）
     */
    private final Integer maxLength;

    FieldType(String description, Integer maxLength) {
        this.description = description;
        this.maxLength = maxLength;
    }

    public String getDescription() {
        return description;
    }

    public Integer getMaxLength() {
        return maxLength;
    }
}
