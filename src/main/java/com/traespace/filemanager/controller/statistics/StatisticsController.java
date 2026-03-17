package com.traespace.filemanager.controller.statistics;

import com.traespace.filemanager.config.UserContext;
import com.traespace.filemanager.dto.response.common.Result;
import com.traespace.filemanager.dto.response.statistics.StatisticsResponse;
import com.traespace.filemanager.service.statistics.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

/**
 * 统计控制器
 *
 * @author Traespace
 * @since 2024-03-17
 */
@Tag(name = "统计接口", description = "数据统计查询")
@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    /**
     * 获取统计数据
     */
    @Operation(summary = "获取统计数据")
    @GetMapping
    public Result<StatisticsResponse> getStatistics() {
        Long userId = UserContext.getUserId();
        StatisticsResponse response = statisticsService.getStatistics(userId);
        return Result.success(response);
    }
}
