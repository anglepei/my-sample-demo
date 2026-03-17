package com.traespace.filemanager.dto.response.statistics;

/**
 * 统计响应DTO
 *
 * @author Traespace
 * @since 2024-03-17
 */
public class StatisticsResponse {

    /**
     * 总文件数
     */
    private Long totalFiles;

    /**
     * 总数据条数
     */
    private Long totalDataCount;

    /**
     * 今日上传文件数
     */
    private Long todayUploadCount;

    /**
     * 今日数据条数
     */
    private Long todayDataCount;

    public StatisticsResponse() {
    }

    public StatisticsResponse(Long totalFiles, Long totalDataCount, Long todayUploadCount, Long todayDataCount) {
        this.totalFiles = totalFiles;
        this.totalDataCount = totalDataCount;
        this.todayUploadCount = todayUploadCount;
        this.todayDataCount = todayDataCount;
    }

    public Long getTotalFiles() {
        return totalFiles;
    }

    public StatisticsResponse setTotalFiles(Long totalFiles) {
        this.totalFiles = totalFiles;
        return this;
    }

    public Long getTotalDataCount() {
        return totalDataCount;
    }

    public StatisticsResponse setTotalDataCount(Long totalDataCount) {
        this.totalDataCount = totalDataCount;
        return this;
    }

    public Long getTodayUploadCount() {
        return todayUploadCount;
    }

    public StatisticsResponse setTodayUploadCount(Long todayUploadCount) {
        this.todayUploadCount = todayUploadCount;
        return this;
    }

    public Long getTodayDataCount() {
        return todayDataCount;
    }

    public StatisticsResponse setTodayDataCount(Long todayDataCount) {
        this.todayDataCount = todayDataCount;
        return this;
    }
}
