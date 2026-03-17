package com.traespace.filemanager.exception;

import com.traespace.filemanager.enums.ErrorCode;

/**
 * 未授权异常
 * 用于标识认证失败或权限不足的情况
 *
 * @author Traespace
 * @since 2024-03-17
 */
public class UnauthorizedException extends BizException {

    /**
     * 构造未授权异常（使用错误码默认消息）
     *
     * @param errorCode 错误码（应该是TOKEN相关的）
     */
    public UnauthorizedException(ErrorCode errorCode) {
        super(errorCode);
    }

    /**
     * 构造未授权异常（自定义消息）
     *
     * @param errorCode 错误码
     * @param message   自定义错误信息
     */
    public UnauthorizedException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
