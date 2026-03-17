package com.traespace.filemanager.dto.request.common;

/**
 * 分页请求基类
 *
 * @author Traespace
 * @since 2024-03-17
 */
public class BasePageRequest {

    /**
     * 页码（从1开始）
     */
    private Integer page = 1;

    /**
     * 每页大小
     */
    private Integer size = 10;

    public BasePageRequest() {
    }

    public BasePageRequest(Integer page, Integer size) {
        this.page = page;
        this.size = size;
    }

    /**
     * 获取偏移量（用于SQL LIMIT）
     */
    public Integer getOffset() {
        return (page - 1) * size;
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
}
