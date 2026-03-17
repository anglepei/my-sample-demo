package com.traespace.filemanager.dto.response.common;

import java.util.List;

/**
 * 分页响应基类
 *
 * @param <T> 列表数据类型
 * @author Traespace
 * @since 2024-03-17
 */
public class BasePageResponse<T> {

    /**
     * 数据列表
     */
    private List<T> list;

    /**
     * 总记录数
     */
    private Long total;

    public BasePageResponse() {
    }

    public BasePageResponse(List<T> list, Long total) {
        this.list = list;
        this.total = total;
    }

    /**
     * 构建分页响应
     */
    public static <T> BasePageResponse<T> build(List<T> list, Long total) {
        return new BasePageResponse<>(list, total);
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }
}
