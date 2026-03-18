package com.traespace.filemanager.vo.file;

import com.fasterxml.jackson.annotation.JsonRawValue;
import com.traespace.filemanager.enums.FileType;

import java.time.LocalDateTime;

/**
 * 文件详情VO
 *
 * @author Traespace
 * @since 2024-03-18
 */
public class FileDetailVO {

    /**
     * 文件ID
     */
    private Long id;

    /**
     * 原始文件名
     */
    private String originalName;

    /**
     * 文件类型
     */
    private FileType fileType;

    /**
     * 数据条数
     */
    private Integer rowCount;

    /**
     * 上传时间
     */
    private LocalDateTime uploadTime;

    /**
     * 上传人用户名
     */
    private String username;

    /**
     * 字段配置快照（JSON字符串）
     */
    @JsonRawValue
    private String fieldConfigSnapshot;

    public FileDetailVO() {
    }

    public Long getId() {
        return id;
    }

    public FileDetailVO setId(Long id) {
        this.id = id;
        return this;
    }

    public String getOriginalName() {
        return originalName;
    }

    public FileDetailVO setOriginalName(String originalName) {
        this.originalName = originalName;
        return this;
    }

    public FileType getFileType() {
        return fileType;
    }

    public FileDetailVO setFileType(FileType fileType) {
        this.fileType = fileType;
        return this;
    }

    public Integer getRowCount() {
        return rowCount;
    }

    public FileDetailVO setRowCount(Integer rowCount) {
        this.rowCount = rowCount;
        return this;
    }

    public LocalDateTime getUploadTime() {
        return uploadTime;
    }

    public FileDetailVO setUploadTime(LocalDateTime uploadTime) {
        this.uploadTime = uploadTime;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public FileDetailVO setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getFieldConfigSnapshot() {
        return fieldConfigSnapshot;
    }

    public FileDetailVO setFieldConfigSnapshot(String fieldConfigSnapshot) {
        this.fieldConfigSnapshot = fieldConfigSnapshot;
        return this;
    }
}
