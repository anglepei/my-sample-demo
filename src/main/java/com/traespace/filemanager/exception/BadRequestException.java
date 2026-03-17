package com.traespace.filemanager.exception;

import com.traespace.filemanager.enums.ErrorCode;

/**
 * 请求参数异常
 * 用于标识客户端请求参数错误的情况
 *
 * @author Traespace
 * @since 2024-03-17
 */
public class BadRequestException extends BizException {

    /**
     * 构造请求参数异常（使用错误码默认消息）
     *
     * @param errorCode 错误码（应该是参数相关的）
     */
    public BadRequestException(ErrorCode errorCode) {
        super(errorCode);
    }

    /**
     * 构造请求参数异常（自定义消息）
     *
     * @param errorCode 错误码
     * @param message   自定义错误信息
     */
    public BadRequestException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
