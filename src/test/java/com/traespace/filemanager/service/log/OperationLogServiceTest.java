package com.traespace.filemanager.service.log;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.traespace.filemanager.dto.request.common.BasePageRequest;
import com.traespace.filemanager.entity.OperationLog;
import com.traespace.filemanager.enums.OperationType;
import com.traespace.filemanager.mapper.OperationLogMapper;
import com.traespace.filemanager.service.impl.OperationLogServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * OperationLogService测试
 */
@ExtendWith(MockitoExtension.class)
class OperationLogServiceTest {

    @Mock
    private OperationLogMapper operationLogMapper;

    @InjectMocks
    private OperationLogServiceImpl operationLogService;

    private Long userId;
    private String username;

    @BeforeEach
    void setUp() {
        userId = 1001L;
        username = "testuser";
    }

    @Test
    void testSaveOperationLog() {
        // 测试保存操作日志
        OperationType operationType = OperationType.UPLOAD;
        String description = "上传文件";
        String requestIp = "127.0.0.1";

        when(operationLogMapper.insert(any(OperationLog.class))).thenReturn(1);

        operationLogService.saveLog(userId, username, operationType, description, requestIp);

        verify(operationLogMapper).insert(any(OperationLog.class));
    }

    @Test
    void testGetOperationLogPage() {
        // 测试获取操作日志分页
        BasePageRequest request = new BasePageRequest();
        request.setPage(1);
        request.setSize(10);

        Page<OperationLog> page = new Page<>(1, 10);
        List<OperationLog> logs = new ArrayList<>();
        OperationLog log = new OperationLog();
        log.setId(1L);
        log.setUserId(userId);
        log.setUsername(username);
        log.setOperationType(OperationType.UPLOAD);
        log.setDescription("上传文件");
        log.setRequestIp("127.0.0.1");
        log.setCreateTime(LocalDateTime.now());
        logs.add(log);
        page.setRecords(logs);
        page.setTotal(1);

        when(operationLogMapper.selectPage(any(), any())).thenReturn(page);

        IPage<com.traespace.filemanager.vo.log.OperationLogItemVO> result =
                operationLogService.getLogPage(userId, request);

        assertThat(result).isNotNull();
        assertThat(result.getRecords()).hasSize(1);
        assertThat(result.getTotal()).isEqualTo(1L);
        verify(operationLogMapper).selectPage(any(), any());
    }

    @Test
    void testGetOperationLogPageWithAllUsers() {
        // 测试获取所有用户的操作日志
        BasePageRequest request = new BasePageRequest();
        request.setPage(1);
        request.setSize(10);

        Page<OperationLog> page = new Page<>(1, 10);
        List<OperationLog> logs = new ArrayList<>();
        page.setRecords(logs);
        page.setTotal(0);

        when(operationLogMapper.selectPage(any(), any())).thenReturn(page);

        IPage<com.traespace.filemanager.vo.log.OperationLogItemVO> result =
                operationLogService.getLogPage(null, request);

        assertThat(result).isNotNull();
        assertThat(result.getRecords()).isEmpty();
        verify(operationLogMapper).selectPage(any(), any());
    }
}
