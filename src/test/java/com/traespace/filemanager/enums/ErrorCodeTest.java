package com.traespace.filemanager.enums;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * ErrorCode枚举测试
 */
class ErrorCodeTest {

    @Test
    void testErrorCodeValues() {
        // 验证成功错误码
        assertThat(ErrorCode.SUCCESS.getCode()).isEqualTo(0);
        assertThat(ErrorCode.SUCCESS.getMessage()).isEqualTo("success");

        // 验证通用错误码
        assertThat(ErrorCode.INVALID_PARAM.getCode()).isEqualTo(1001);
        assertThat(ErrorCode.TOKEN_MISSING.getCode()).isEqualTo(1002);
        assertThat(ErrorCode.TOKEN_EXPIRED.getCode()).isEqualTo(1003);
        assertThat(ErrorCode.PERMISSION_DENIED.getCode()).isEqualTo(1004);

        // 验证用户相关错误码
        assertThat(ErrorCode.USER_EXISTS.getCode()).isEqualTo(2001);
        assertThat(ErrorCode.USER_NOT_FOUND.getCode()).isEqualTo(2002);
        assertThat(ErrorCode.PASSWORD_ERROR.getCode()).isEqualTo(2003);

        // 验证文件相关错误码
        assertThat(ErrorCode.FILE_FORMAT_ERROR.getCode()).isEqualTo(3001);
        assertThat(ErrorCode.FILE_SIZE_EXCEED.getCode()).isEqualTo(3002);
        assertThat(ErrorCode.FILE_HEADER_MISMATCH.getCode()).isEqualTo(3003);

        // 验证数据校验错误码
        assertThat(ErrorCode.SEQ_NO_EMPTY.getCode()).isEqualTo(4001);
        assertThat(ErrorCode.ID_CARD_ERROR.getCode()).isEqualTo(4002);
        assertThat(ErrorCode.PHONE_ERROR.getCode()).isEqualTo(4003);
        assertThat(ErrorCode.FIELD_REQUIRED.getCode()).isEqualTo(4004);
        assertThat(ErrorCode.FIELD_TYPE_ERROR.getCode()).isEqualTo(4005);
        assertThat(ErrorCode.ID_CARD_DUPLICATE.getCode()).isEqualTo(4006);

        // 验证系统错误码
        assertThat(ErrorCode.SYSTEM_ERROR.getCode()).isEqualTo(5001);
        assertThat(ErrorCode.DB_ERROR.getCode()).isEqualTo(5002);
    }

    @Test
    void testErrorCodeGetters() {
        ErrorCode errorCode = ErrorCode.INVALID_PARAM;
        assertThat(errorCode.getCode()).isEqualTo(1001);
        assertThat(errorCode.getMessage()).isEqualTo("参数错误");
    }
}
