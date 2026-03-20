package com.traespace.filemanager.vo.log;

import com.traespace.filemanager.enums.OperationType;

import java.time.LocalDateTime;

/**
 * 操作日志项VO
 *
 * @author Traespace
 * @since 2024-03-17
 */
public class OperationLogItemVO {

    /**
     * 日志ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 操作类型
     */
    private OperationType operationType;

    /**
     * 操作描述
     */
    private String description;

    /**
     * 请求IP
     */
    private String requestIp;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 耗时（毫秒）
     */
    private Long costTime;

    public Long getId() {
        return id;
    }

    public OperationLogItemVO setId(Long id) {
        this.id = id;
        return this;
    }

    public Long getUserId() {
        return userId;
    }

    public OperationLogItemVO setUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public OperationLogItemVO setUsername(String username) {
        this.username = username;
        return this;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    public OperationLogItemVO setOperationType(OperationType operationType) {
        this.operationType = operationType;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public OperationLogItemVO setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getRequestIp() {
        return requestIp;
    }

    public OperationLogItemVO setRequestIp(String requestIp) {
        this.requestIp = requestIp;
        return this;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public OperationLogItemVO setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
        return this;
    }

    public Long getCostTime() {
        return costTime;
    }

    public OperationLogItemVO setCostTime(Long costTime) {
        this.costTime = costTime;
        return this;
    }
}
