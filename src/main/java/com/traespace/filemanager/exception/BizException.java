package com.traespace.filemanager.exception;

import com.traespace.filemanager.enums.ErrorCode;

/**
 * 业务异常基类
 *
 * @author Traespace
 * @since 2024-03-17
 */
public class BizException extends RuntimeException {

    /**
     * 错误码
     */
    private final ErrorCode errorCode;

    /**
     * 构造业务异常（使用错误码默认消息）
     *
     * @param errorCode 错误码
     */
    public BizException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    /**
     * 构造业务异常（自定义消息）
     *
     * @param errorCode 错误码
     * @param message   自定义错误信息
     */
    public BizException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
