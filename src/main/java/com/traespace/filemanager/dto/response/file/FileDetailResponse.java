package com.traespace.filemanager.dto.response.file;

import com.traespace.filemanager.vo.file.DataDetailItemVO;
import com.traespace.filemanager.vo.file.FileDetailVO;

import java.util.List;

/**
 * 文件详情响应
 *
 * @author Traespace
 * @since 2024-03-17
 */
public class FileDetailResponse {

    /**
     * 文件详情
     */
    private FileDetailVO file;

    /**
     * 数据详情列表
     */
    private List<DataDetailItemVO> list;

    /**
     * 当前页码
     */
    private Integer page;

    /**
     * 每页大小
     */
    private Integer size;

    /**
     * 总条数
     */
    private Long total;

    public FileDetailResponse() {
    }

    public FileDetailResponse(FileDetailVO file, List<DataDetailItemVO> list, Integer page, Integer size, Long total) {
        this.file = file;
        this.list = list;
        this.page = page;
        this.size = size;
        this.total = total;
    }

    public FileDetailVO getFile() {
        return file;
    }

    public void setFile(FileDetailVO file) {
        this.file = file;
    }

    public List<DataDetailItemVO> getList() {
        return list;
    }

    public void setList(List<DataDetailItemVO> list) {
        this.list = list;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    // 保留旧方法以兼容
    @Deprecated
    public Long getFileId() {
        return file != null ? file.getId() : null;
    }

    @Deprecated
    public String getFileName() {
        return file != null ? file.getOriginalName() : null;
    }

    @Deprecated
    public List<DataDetailItemVO> getDetails() {
        return list;
    }
}
