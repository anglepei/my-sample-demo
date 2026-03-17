package com.traespace.filemanager.exception;

import com.traespace.filemanager.enums.ErrorCode;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * BizException业务异常测试
 */
class BizExceptionTest {

    @Test
    void testCreateWithErrorCode() {
        ErrorCode errorCode = ErrorCode.INVALID_PARAM;
        BizException exception = new BizException(errorCode);

        assertThat(exception.getErrorCode()).isEqualTo(errorCode);
        assertThat(exception.getMessage()).isEqualTo(errorCode.getMessage());
    }

    @Test
    void testCreateWithErrorCodeAndMessage() {
        ErrorCode errorCode = ErrorCode.INVALID_PARAM;
        String customMessage = "自定义错误信息";
        BizException exception = new BizException(errorCode, customMessage);

        assertThat(exception.getErrorCode()).isEqualTo(errorCode);
        assertThat(exception.getMessage()).isEqualTo(customMessage);
    }

    @Test
    void testThrowBizException() {
        assertThatThrownBy(() -> {
            throw new BizException(ErrorCode.USER_NOT_FOUND);
        })
                .isInstanceOf(BizException.class)
                .hasMessage(ErrorCode.USER_NOT_FOUND.getMessage());
    }
}
