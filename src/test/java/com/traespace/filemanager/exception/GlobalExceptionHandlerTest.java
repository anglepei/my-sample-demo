package com.traespace.filemanager.exception;

import com.traespace.filemanager.enums.ErrorCode;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * GlobalExceptionHandler全局异常处理器测试
 */
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

    @Test
    void testHandlerExists() {
        // 测试异常处理器存在
        assertThat(exceptionHandler).isNotNull();
    }

    @Test
    void testHandleBizExceptionReturnsResult() {
        // 测试业务异常处理返回Result
        BizException exception = new BizException(ErrorCode.USER_NOT_FOUND);
        var result = exceptionHandler.handleBizException(exception);

        assertThat(result).isNotNull();
        assertThat(result.getCode()).isEqualTo(ErrorCode.USER_NOT_FOUND.getCode());
        assertThat(result.getMessage()).isEqualTo(ErrorCode.USER_NOT_FOUND.getMessage());
    }

    @Test
    void testHandleSystemExceptionReturnsResult() {
        // 测试系统异常处理返回Result
        Exception exception = new RuntimeException("系统错误");
        var result = exceptionHandler.handleException(exception);

        assertThat(result).isNotNull();
        assertThat(result.getCode()).isEqualTo(ErrorCode.SYSTEM_ERROR.getCode());
        assertThat(result.getMessage()).isEqualTo("系统内部错误");
    }
}
