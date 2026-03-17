package com.traespace.filemanager.exception;

import com.traespace.filemanager.enums.ErrorCode;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * UnauthorizedException未授权异常测试
 */
class UnauthorizedExceptionTest {

    @Test
    void testCreateWithErrorCode() {
        ErrorCode errorCode = ErrorCode.TOKEN_MISSING;
        UnauthorizedException exception = new UnauthorizedException(errorCode);

        assertThat(exception.getErrorCode()).isEqualTo(errorCode);
        assertThat(exception.getMessage()).isEqualTo(errorCode.getMessage());
    }

    @Test
    void testCreateWithErrorCodeAndMessage() {
        ErrorCode errorCode = ErrorCode.TOKEN_EXPIRED;
        String customMessage = "Token已过期，请重新登录";
        UnauthorizedException exception = new UnauthorizedException(errorCode, customMessage);

        assertThat(exception.getErrorCode()).isEqualTo(errorCode);
        assertThat(exception.getMessage()).isEqualTo(customMessage);
    }

    @Test
    void testIsBizException() {
        UnauthorizedException exception = new UnauthorizedException(ErrorCode.PERMISSION_DENIED);

        assertThat(exception).isInstanceOf(BizException.class);
    }

    @Test
    void testThrowUnauthorizedException() {
        assertThatThrownBy(() -> {
            throw new UnauthorizedException(ErrorCode.PERMISSION_DENIED);
        })
                .isInstanceOf(UnauthorizedException.class)
                .isInstanceOf(BizException.class)
                .hasMessage(ErrorCode.PERMISSION_DENIED.getMessage());
    }
}
