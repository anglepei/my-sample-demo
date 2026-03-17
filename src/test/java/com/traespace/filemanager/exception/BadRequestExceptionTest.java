package com.traespace.filemanager.exception;

import com.traespace.filemanager.enums.ErrorCode;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * BadRequestException请求参数异常测试
 */
class BadRequestExceptionTest {

    @Test
    void testCreateWithErrorCode() {
        ErrorCode errorCode = ErrorCode.INVALID_PARAM;
        BadRequestException exception = new BadRequestException(errorCode);

        assertThat(exception.getErrorCode()).isEqualTo(errorCode);
        assertThat(exception.getMessage()).isEqualTo(errorCode.getMessage());
    }

    @Test
    void testCreateWithErrorCodeAndMessage() {
        ErrorCode errorCode = ErrorCode.INVALID_PARAM;
        String customMessage = "用户名不能为空";
        BadRequestException exception = new BadRequestException(errorCode, customMessage);

        assertThat(exception.getErrorCode()).isEqualTo(errorCode);
        assertThat(exception.getMessage()).isEqualTo(customMessage);
    }

    @Test
    void testIsBizException() {
        BadRequestException exception = new BadRequestException(ErrorCode.FIELD_REQUIRED);

        assertThat(exception).isInstanceOf(BizException.class);
    }

    @Test
    void testThrowBadRequestException() {
        assertThatThrownBy(() -> {
            throw new BadRequestException(ErrorCode.ID_CARD_ERROR);
        })
                .isInstanceOf(BadRequestException.class)
                .isInstanceOf(BizException.class)
                .hasMessage(ErrorCode.ID_CARD_ERROR.getMessage());
    }
}
