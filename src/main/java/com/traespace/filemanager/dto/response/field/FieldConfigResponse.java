package com.traespace.filemanager.dto.response.field;

import com.traespace.filemanager.dto.request.field.FieldConfigItem;

import java.util.List;

/**
 * 字段配置响应
 *
 * @author Traespace
 * @since 2024-03-17
 */
public class FieldConfigResponse {

    /**
     * 字段列表
     */
    private List<FieldConfigItem> fields;

    public FieldConfigResponse() {
    }

    public FieldConfigResponse(List<FieldConfigItem> fields) {
        this.fields = fields;
    }

    public List<FieldConfigItem> getFields() {
        return fields;
    }

    public void setFields(List<FieldConfigItem> fields) {
        this.fields = fields;
    }
}
