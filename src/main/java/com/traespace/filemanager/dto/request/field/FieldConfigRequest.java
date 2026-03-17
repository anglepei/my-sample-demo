package com.traespace.filemanager.dto.request.field;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * 字段配置请求
 *
 * @author Traespace
 * @since 2024-03-17
 */
public class FieldConfigRequest {

    /**
     * 字段列表
     */
    @Valid
    @NotNull(message = "字段列表不能为空")
    @NotEmpty(message = "字段列表不能为空")
    @Size(min = 1, max = 10, message = "字段数量范围为1-10个")
    private List<FieldConfigItem> fields;

    public List<FieldConfigItem> getFields() {
        return fields;
    }

    public void setFields(List<FieldConfigItem> fields) {
        this.fields = fields;
    }
}
