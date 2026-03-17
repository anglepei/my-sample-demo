package com.traespace.filemanager.controller.log;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.traespace.filemanager.config.UserContext;
import com.traespace.filemanager.dto.request.common.BasePageRequest;
import com.traespace.filemanager.dto.response.common.Result;
import com.traespace.filemanager.enums.OperationType;
import com.traespace.filemanager.service.log.OperationLogService;
import com.traespace.filemanager.vo.log.OperationLogItemVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * OperationLogController测试
 */
@ExtendWith(MockitoExtension.class)
class OperationLogControllerTest {

    @Mock
    private OperationLogService operationLogService;

    @InjectMocks
    private OperationLogController operationLogController;

    private Long userId;
    private IPage<OperationLogItemVO> mockPage;

    @BeforeEach
    void setUp() {
        userId = 1001L;

        // 准备分页数据
        List<OperationLogItemVO> records = new ArrayList<>();
        OperationLogItemVO vo = new OperationLogItemVO();
        vo.setId(1L);
        vo.setUserId(userId);
        vo.setUsername("testuser");
        vo.setOperationType(OperationType.UPLOAD);
        vo.setDescription("上传文件");
        vo.setRequestIp("127.0.0.1");
        vo.setCreateTime(LocalDateTime.now());
        records.add(vo);

        mockPage = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(1, 10, 1);
        ((com.baomidou.mybatisplus.extension.plugins.pagination.Page<OperationLogItemVO>) mockPage).setRecords(records);
    }

    @Test
    void testGetOperationLogs() {
        // 测试获取操作日志列表
        BasePageRequest request = new BasePageRequest();
        request.setPage(1);
        request.setSize(10);

        when(operationLogService.getLogPage(eq(userId), any())).thenReturn(mockPage);

        try (MockedStatic<UserContext> userContextMock = mockStatic(UserContext.class)) {
            userContextMock.when(UserContext::getUserId).thenReturn(userId);

            Result<IPage<OperationLogItemVO>> result = operationLogController.getOperationLogs(request);

            assertThat(result).isNotNull();
            assertThat(result.getCode()).isEqualTo(0);
            assertThat(result.getData()).isNotNull();
            assertThat(result.getData().getRecords()).hasSize(1);

            verify(operationLogService).getLogPage(eq(userId), any());
        }
    }

    @Test
    void testGetAllOperationLogs() {
        // 测试获取所有用户的操作日志（管理员）
        Long adminId = 1L;
        BasePageRequest request = new BasePageRequest();
        request.setPage(1);
        request.setSize(10);

        when(operationLogService.getLogPage(eq(null), any())).thenReturn(mockPage);

        try (MockedStatic<UserContext> userContextMock = mockStatic(UserContext.class)) {
            userContextMock.when(UserContext::getUserId).thenReturn(adminId);

            Result<IPage<OperationLogItemVO>> result = operationLogController.getAllOperationLogs(request);

            assertThat(result).isNotNull();
            assertThat(result.getCode()).isEqualTo(0);
            assertThat(result.getData()).isNotNull();

            verify(operationLogService).getLogPage(eq(null), any());
        }
    }

    @Test
    void testGetOperationLogsWithDifferentPage() {
        // 测试不同页码的操作日志查询
        BasePageRequest request = new BasePageRequest();
        request.setPage(2);
        request.setSize(20);

        IPage<OperationLogItemVO> emptyPage = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(2, 20, 0);
        ((com.baomidou.mybatisplus.extension.plugins.pagination.Page<OperationLogItemVO>) emptyPage).setRecords(new ArrayList<>());

        when(operationLogService.getLogPage(eq(userId), any())).thenReturn(emptyPage);

        try (MockedStatic<UserContext> userContextMock = mockStatic(UserContext.class)) {
            userContextMock.when(UserContext::getUserId).thenReturn(userId);

            Result<IPage<OperationLogItemVO>> result = operationLogController.getOperationLogs(request);

            assertThat(result).isNotNull();
            assertThat(result.getData().getRecords()).isEmpty();

            verify(operationLogService).getLogPage(eq(userId), any());
        }
    }
}
