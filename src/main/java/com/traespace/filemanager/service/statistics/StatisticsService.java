package com.traespace.filemanager.service.statistics;

import com.traespace.filemanager.dto.response.statistics.StatisticsResponse;

/**
 * 统计服务接口
 *
 * @author Traespace
 * @since 2024-03-17
 */
public interface StatisticsService {

    /**
     * 获取统计数据
     *
     * @param userId 用户ID
     * @return 统计响应
     */
    StatisticsResponse getStatistics(Long userId);
}
