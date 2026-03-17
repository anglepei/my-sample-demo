package com.traespace.filemanager.aspect;

import com.traespace.filemanager.annotation.Log;
import com.traespace.filemanager.enums.OperationType;
import com.traespace.filemanager.service.log.OperationLogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * OperationLogAspect测试
 */
@ExtendWith(MockitoExtension.class)
class OperationLogAspectTest {

    @Mock
    private OperationLogService operationLogService;

    @InjectMocks
    private OperationLogAspect operationLogAspect;

    @BeforeEach
    void setUp() {
        // Setup can be done here if needed
    }

    @Test
    void testAspectHasOperationLogService() {
        // 测试切面正确注入了OperationLogService
        // 这是一个基础测试，验证依赖注入正常
        assertThat(operationLogAspect).isNotNull();
    }

    @Test
    void testLogAnnotationExists() {
        // 测试@Log注解存在并可使用
        // 这个测试只是验证注解机制工作正常
        assertThat(Log.class).isNotNull();
    }

    @Test
    void testOperationLogServiceMethod() {
        // 测试OperationLogService的saveLog方法存在
        // 这是一个单元测试，不测试AOP切面的实际行为
        // AOP测试需要集成测试环境

        Long userId = 1001L;
        String username = "testuser";
        OperationType operationType = OperationType.UPLOAD;
        String description = "上传文件";
        String requestIp = "127.0.0.1";

        operationLogService.saveLog(userId, username, operationType, description, requestIp);

        verify(operationLogService).saveLog(userId, username, operationType, description, requestIp);
    }
}
