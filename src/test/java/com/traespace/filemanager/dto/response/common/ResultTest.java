package com.traespace.filemanager.dto.response.common;

import com.traespace.filemanager.enums.ErrorCode;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Result统一响应结果测试
 */
class ResultTest {

    @Test
    void testSuccess() {
        // 测试成功响应（无数据）
        Result<Void> result = Result.success();
        assertThat(result.getCode()).isEqualTo(0);
        assertThat(result.getMessage()).isEqualTo("success");
        assertThat(result.getData()).isNull();
    }

    @Test
    void testSuccessWithData() {
        // 测试成功响应（带数据）- 使用Integer避免String重载歧义
        Integer data = 123;
        Result<Integer> result = Result.success(data);

        assertThat(result.getCode()).isEqualTo(0);
        assertThat(result.getMessage()).isEqualTo("success");
        assertThat(result.getData()).isEqualTo(data);
    }

    @Test
    void testSuccessWithMessage() {
        // 测试成功响应（自定义消息）
        String customMessage = "操作成功";
        Result<Void> result = Result.success(customMessage);

        assertThat(result.getCode()).isEqualTo(0);
        assertThat(result.getMessage()).isEqualTo(customMessage);
        assertThat(result.getData()).isNull();
    }

    @Test
    void testErrorWithCode() {
        // 测试错误响应（仅错误码）
        Result<Void> result = Result.error(ErrorCode.USER_NOT_FOUND);
        assertThat(result.getCode()).isEqualTo(ErrorCode.USER_NOT_FOUND.getCode());
        assertThat(result.getMessage()).isEqualTo(ErrorCode.USER_NOT_FOUND.getMessage());
        assertThat(result.getData()).isNull();
    }

    @Test
    void testErrorWithCodeAndMessage() {
        // 测试错误响应（错误码+自定义消息）
        String customMessage = "用户不存在";
        Result<Void> result = Result.error(ErrorCode.USER_NOT_FOUND, customMessage);
        assertThat(result.getCode()).isEqualTo(ErrorCode.USER_NOT_FOUND.getCode());
        assertThat(result.getMessage()).isEqualTo(customMessage);
        assertThat(result.getData()).isNull();
    }

    @Test
    void testErrorWithCodeAndMessageAndData() {
        // 测试错误响应（错误码+自定义消息+数据）
        String customMessage = "参数错误";
        String data = "field";
        Result<String> result = Result.error(ErrorCode.INVALID_PARAM, customMessage, data);
        assertThat(result.getCode()).isEqualTo(ErrorCode.INVALID_PARAM.getCode());
        assertThat(result.getMessage()).isEqualTo(customMessage);
        assertThat(result.getData()).isEqualTo(data);
    }

    @Test
    void testSetters() {
        // 测试setter方法
        Result<String> result = new Result<>();
        result.setCode(100);
        result.setMessage("custom message");
        result.setData("data");

        assertThat(result.getCode()).isEqualTo(100);
        assertThat(result.getMessage()).isEqualTo("custom message");
        assertThat(result.getData()).isEqualTo("data");
    }
}
