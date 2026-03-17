package com.traespace.filemanager.test;

import com.traespace.filemanager.dto.response.common.Result;
import com.traespace.filemanager.enums.ErrorCode;

public class ResultDebugTest {
    public static void main(String[] args) {
        System.out.println("=== Testing Result class ===");

        // 测试1: success()
        Result<Void> r1 = Result.success();
        System.out.println("Test 1 - success():");
        System.out.println("  Code: " + r1.getCode());
        System.out.println("  Message: " + r1.getMessage());
        System.out.println("  Data: " + r1.getData());

        // 测试2: success(data)
        String data = "测试数据";
        Result<String> r2 = Result.success(data);
        System.out.println("\nTest 2 - success(data):");
        System.out.println("  Code: " + r2.getCode());
        System.out.println("  Message: " + r2.getMessage());
        System.out.println("  Data: " + r2.getData());
        System.out.println("  Expected data: " + data);

        // 测试3: 检查ErrorCode
        System.out.println("\nTest 3 - ErrorCode:");
        System.out.println("  ErrorCode.SUCCESS: " + ErrorCode.SUCCESS);
        System.out.println("  ErrorCode.SUCCESS.getCode(): " + ErrorCode.SUCCESS.getCode());
        System.out.println("  ErrorCode.SUCCESS.getMessage(): " + ErrorCode.SUCCESS.getMessage());
    }
}
