package com.traespace.filemanager.vo.file;

import com.traespace.filemanager.enums.FileType;

import java.time.LocalDateTime;

/**
 * 文件列表项VO
 *
 * @author Traespace
 * @since 2024-03-17
 */
public class FileListItemVO {

    /**
     * 文件ID
     */
    private Long id;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文件类型
     */
    private FileType fileType;

    /**
     * 状态（1:正常 0:已删除）
     */
    private Integer status;

    /**
     * 上传时间
     */
    private LocalDateTime uploadTime;

    /**
     * 数据条数
     */
    private Integer dataCount;

    public FileListItemVO() {
    }

    public FileListItemVO(Long id, String fileName, FileType fileType, Integer status,
                           LocalDateTime uploadTime, Integer dataCount) {
        this.id = id;
        this.fileName = fileName;
        this.fileType = fileType;
        this.status = status;
        this.uploadTime = uploadTime;
        this.dataCount = dataCount;
    }

    public Long getId() {
        return id;
    }

    public FileListItemVO setId(Long id) {
        this.id = id;
        return this;
    }

    public String getFileName() {
        return fileName;
    }

    public FileListItemVO setFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public FileType getFileType() {
        return fileType;
    }

    public FileListItemVO setFileType(FileType fileType) {
        this.fileType = fileType;
        return this;
    }

    public Integer getStatus() {
        return status;
    }

    public FileListItemVO setStatus(Integer status) {
        this.status = status;
        return this;
    }

    public LocalDateTime getUploadTime() {
        return uploadTime;
    }

    public FileListItemVO setUploadTime(LocalDateTime uploadTime) {
        this.uploadTime = uploadTime;
        return this;
    }

    public Integer getDataCount() {
        return dataCount;
    }

    public FileListItemVO setDataCount(Integer dataCount) {
        this.dataCount = dataCount;
        return this;
    }
}
