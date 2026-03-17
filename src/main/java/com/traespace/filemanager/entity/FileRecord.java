package com.traespace.filemanager.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.traespace.filemanager.enums.FileType;
import java.time.LocalDateTime;

/**
 * 文件记录实体
 *
 * @author Traespace
 * @since 2024-03-17
 */
@TableName("t_file_record")
public class FileRecord {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 文件名（系统生成）
     */
    @TableField("file_name")
    private String fileName;

    /**
     * 原始文件名
     */
    @TableField("original_name")
    private String originalName;

    /**
     * 上传人ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 文件类型
     */
    @TableField("file_type")
    private FileType fileType;

    /**
     * 状态（1:正常 0:已删除）
     */
    @TableField("status")
    private Integer status;

    /**
     * 数据条数
     */
    @TableField("row_count")
    private Integer rowCount;

    /**
     * 字段配置快照（JSON）
     */
    @TableField("field_config_snapshot")
    private String fieldConfigSnapshot;

    /**
     * 上传时间
     */
    @TableField("upload_time")
    private LocalDateTime uploadTime;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public FileType getFileType() {
        return fileType;
    }

    public void setFileType(FileType fileType) {
        this.fileType = fileType;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getRowCount() {
        return rowCount;
    }

    public void setRowCount(Integer rowCount) {
        this.rowCount = rowCount;
    }

    public String getFieldConfigSnapshot() {
        return fieldConfigSnapshot;
    }

    public void setFieldConfigSnapshot(String fieldConfigSnapshot) {
        this.fieldConfigSnapshot = fieldConfigSnapshot;
    }

    public LocalDateTime getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(LocalDateTime uploadTime) {
        this.uploadTime = uploadTime;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}
