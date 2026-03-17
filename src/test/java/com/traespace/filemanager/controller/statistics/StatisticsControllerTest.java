package com.traespace.filemanager.controller.statistics;

import com.traespace.filemanager.config.UserContext;
import com.traespace.filemanager.dto.response.common.Result;
import com.traespace.filemanager.dto.response.statistics.StatisticsResponse;
import com.traespace.filemanager.service.statistics.StatisticsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * StatisticsController测试
 */
@ExtendWith(MockitoExtension.class)
class StatisticsControllerTest {

    @Mock
    private StatisticsService statisticsService;

    @InjectMocks
    private StatisticsController statisticsController;

    private Long userId;
    private StatisticsResponse mockResponse;

    @BeforeEach
    void setUp() {
        userId = 1001L;

        mockResponse = new StatisticsResponse();
        mockResponse.setTotalFiles(100L);
        mockResponse.setTotalDataCount(10000L);
        mockResponse.setTodayUploadCount(5L);
        mockResponse.setTodayDataCount(500L);
    }

    @Test
    void testGetStatistics() {
        // 测试获取统计数据
        when(statisticsService.getStatistics(userId)).thenReturn(mockResponse);

        try (MockedStatic<UserContext> userContextMock = mockStatic(UserContext.class)) {
            userContextMock.when(UserContext::getUserId).thenReturn(userId);

            Result<StatisticsResponse> result = statisticsController.getStatistics();

            assertThat(result).isNotNull();
            assertThat(result.getCode()).isEqualTo(0);
            assertThat(result.getData()).isNotNull();
            assertThat(result.getData().getTotalFiles()).isEqualTo(100L);
            assertThat(result.getData().getTotalDataCount()).isEqualTo(10000L);

            verify(statisticsService).getStatistics(userId);
        }
    }

    @Test
    void testGetStatisticsWithZeroData() {
        // 测试无数据情况
        StatisticsResponse emptyResponse = new StatisticsResponse();
        emptyResponse.setTotalFiles(0L);
        emptyResponse.setTotalDataCount(0L);
        emptyResponse.setTodayUploadCount(0L);
        emptyResponse.setTodayDataCount(0L);

        when(statisticsService.getStatistics(userId)).thenReturn(emptyResponse);

        try (MockedStatic<UserContext> userContextMock = mockStatic(UserContext.class)) {
            userContextMock.when(UserContext::getUserId).thenReturn(userId);

            Result<StatisticsResponse> result = statisticsController.getStatistics();

            assertThat(result).isNotNull();
            assertThat(result.getData().getTotalFiles()).isZero();
            assertThat(result.getData().getTotalDataCount()).isZero();

            verify(statisticsService).getStatistics(userId);
        }
    }

    @Test
    void testGetStatisticsServiceExists() {
        // 测试服务存在
        assertThat(statisticsController).isNotNull();
    }
}
