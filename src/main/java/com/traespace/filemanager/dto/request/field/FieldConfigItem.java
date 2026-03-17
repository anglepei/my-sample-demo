package com.traespace.filemanager.dto.request.field;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/**
 * 字段配置项
 *
 * @author Traespace
 * @since 2024-03-17
 */
public class FieldConfigItem {

    /**
     * 字段名称
     */
    @NotBlank(message = "字段名称不能为空")
    @Pattern(regexp = "^[\\u4e00-\\u9fa5a-zA-Z0-9_]{1,20}$", message = "字段名称为1-20位中文、字母、数字或下划线")
    private String fieldName;

    /**
     * 字段类型（TEXT/NUMBER/DATE）
     */
    @NotBlank(message = "字段类型不能为空")
    @Pattern(regexp = "^(TEXT|NUMBER|DATE)$", message = "字段类型必须是TEXT、NUMBER或DATE")
    private String fieldType;

    /**
     * 是否必填
     */
    @NotNull(message = "必填标识不能为空")
    private Boolean required;

    /**
     * 排序顺序
     */
    private Integer sortOrder;

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }
}
