package com.traespace.filemanager.dto.response.file;

import com.traespace.filemanager.dto.response.common.BasePageResponse;
import com.traespace.filemanager.vo.file.FileListItemVO;

import java.util.List;

/**
 * 文件列表响应
 *
 * @author Traespace
 * @since 2024-03-17
 */
public class FileListResponse extends BasePageResponse<FileListItemVO> {

    /**
     * 文件列表
     */
    private List<FileListItemVO> records;

    /**
     * 总数
     */
    private Long total;

    public List<FileListItemVO> getRecords() {
        return records;
    }

    public void setRecords(List<FileListItemVO> records) {
        this.records = records;
    }

    @Override
    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }
}
