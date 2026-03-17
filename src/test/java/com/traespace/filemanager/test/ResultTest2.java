package com.traespace.filemanager.test;

import com.traespace.filemanager.dto.response.common.Result;
import com.traespace.filemanager.enums.ErrorCode;

public class ResultTest2 {
    public static void main(String[] args) {
        // 直接测试构造函数
        System.out.println("=== Testing Constructor ===");
        String data = "测试数据";

        System.out.println("Input data: " + data);
        System.out.println("ErrorCode.SUCCESS.getCode(): " + ErrorCode.SUCCESS.getCode());
        System.out.println("ErrorCode.SUCCESS.getMessage(): " + ErrorCode.SUCCESS.getMessage());

        // 直接调用构造函数
        Result<String> result = new Result<>(ErrorCode.SUCCESS.getCode(), ErrorCode.SUCCESS.getMessage(), data);

        System.out.println("\nResult after construction:");
        System.out.println("  result.getCode(): " + result.getCode());
        System.out.println("  result.getMessage(): " + result.getMessage());
        System.out.println("  result.getData(): " + result.getData());

        // 验证
        System.out.println("\nValidation:");
        System.out.println("  Code is 0: " + (result.getCode() == 0));
        System.out.println("  Message is 'success': " + ("success".equals(result.getMessage())));
        System.out.println("  Data is input: " + (data.equals(result.getData())));
    }
}
