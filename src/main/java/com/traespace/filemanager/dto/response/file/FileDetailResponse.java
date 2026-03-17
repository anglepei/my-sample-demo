package com.traespace.filemanager.dto.response.file;

import com.traespace.filemanager.vo.file.DataDetailItemVO;

import java.util.List;

/**
 * 文件详情响应
 *
 * @author Traespace
 * @since 2024-03-17
 */
public class FileDetailResponse {

    /**
     * 文件ID
     */
    private Long fileId;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 数据详情列表
     */
    private List<DataDetailItemVO> details;

    /**
     * 总条数
     */
    private Long total;

    public FileDetailResponse() {
    }

    public FileDetailResponse(Long fileId, String fileName, List<DataDetailItemVO> details, Long total) {
        this.fileId = fileId;
        this.fileName = fileName;
        this.details = details;
        this.total = total;
    }

    public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public List<DataDetailItemVO> getDetails() {
        return details;
    }

    public void setDetails(List<DataDetailItemVO> details) {
        this.details = details;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }
}
