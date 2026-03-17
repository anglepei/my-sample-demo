package com.traespace.filemanager.dto.response.statistics;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * StatisticsResponse测试
 */
class StatisticsResponseTest {

    @Test
    void testStatisticsResponse() {
        // 测试统计响应
        StatisticsResponse response = new StatisticsResponse();
        response.setTotalFiles(100L);
        response.setTotalDataCount(10000L);
        response.setTodayUploadCount(5L);
        response.setTodayDataCount(500L);

        assertThat(response.getTotalFiles()).isEqualTo(100L);
        assertThat(response.getTotalDataCount()).isEqualTo(10000L);
        assertThat(response.getTodayUploadCount()).isEqualTo(5L);
        assertThat(response.getTodayDataCount()).isEqualTo(500L);
    }

    @Test
    void testConstructor() {
        // 测试构造函数
        StatisticsResponse response = new StatisticsResponse(200L, 20000L, 10L, 1000L);

        assertThat(response.getTotalFiles()).isEqualTo(200L);
        assertThat(response.getTotalDataCount()).isEqualTo(20000L);
        assertThat(response.getTodayUploadCount()).isEqualTo(10L);
        assertThat(response.getTodayDataCount()).isEqualTo(1000L);
    }

    @Test
    void testBuilderPattern() {
        // 测试链式调用
        StatisticsResponse response = new StatisticsResponse()
                .setTotalFiles(50L)
                .setTotalDataCount(5000L)
                .setTodayUploadCount(3L)
                .setTodayDataCount(300L);

        assertThat(response.getTotalFiles()).isEqualTo(50L);
        assertThat(response.getTotalDataCount()).isEqualTo(5000L);
        assertThat(response.getTodayUploadCount()).isEqualTo(3L);
        assertThat(response.getTodayDataCount()).isEqualTo(300L);
    }

    @Test
    void testZeroValues() {
        // 测试零值情况
        StatisticsResponse response = new StatisticsResponse();
        response.setTotalFiles(0L);
        response.setTotalDataCount(0L);
        response.setTodayUploadCount(0L);
        response.setTodayDataCount(0L);

        assertThat(response.getTotalFiles()).isZero();
        assertThat(response.getTotalDataCount()).isZero();
        assertThat(response.getTodayUploadCount()).isZero();
        assertThat(response.getTodayDataCount()).isZero();
    }
}
