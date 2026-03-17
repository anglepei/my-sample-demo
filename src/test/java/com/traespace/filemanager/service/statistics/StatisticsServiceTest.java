package com.traespace.filemanager.service.statistics;

import com.traespace.filemanager.dto.response.statistics.StatisticsResponse;
import com.traespace.filemanager.mapper.DataDetailMapper;
import com.traespace.filemanager.mapper.FileRecordMapper;
import com.traespace.filemanager.service.impl.StatisticsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * StatisticsService测试
 */
@ExtendWith(MockitoExtension.class)
class StatisticsServiceTest {

    @Mock
    private FileRecordMapper fileRecordMapper;

    @Mock
    private DataDetailMapper dataDetailMapper;

    @InjectMocks
    private StatisticsServiceImpl statisticsService;

    private Long userId;

    @BeforeEach
    void setUp() {
        userId = 1001L;
    }

    @Test
    void testGetStatisticsWithNoData() {
        // 测试无数据情况
        when(fileRecordMapper.selectCount(any())).thenReturn(0L);
        when(fileRecordMapper.selectList(any())).thenReturn(new ArrayList<>());

        StatisticsResponse response = statisticsService.getStatistics(userId);

        assertThat(response).isNotNull();
        assertThat(response.getTotalFiles()).isZero();
        assertThat(response.getTotalDataCount()).isZero();
    }

    @Test
    void testGetStatisticsServiceExists() {
        // 测试服务接口存在
        assertThat(statisticsService).isNotNull();
    }

    @Test
    void testGetStatisticsReturnsResponse() {
        // 测试返回正确的响应类型
        when(fileRecordMapper.selectCount(any())).thenReturn(0L);
        when(fileRecordMapper.selectList(any())).thenReturn(new ArrayList<>());

        StatisticsResponse response = statisticsService.getStatistics(userId);

        assertThat(response).isNotNull();
        assertThat(response).isExactlyInstanceOf(StatisticsResponse.class);
    }
}
