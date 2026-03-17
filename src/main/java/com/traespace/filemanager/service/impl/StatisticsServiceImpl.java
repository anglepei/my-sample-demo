package com.traespace.filemanager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.traespace.filemanager.dto.response.statistics.StatisticsResponse;
import com.traespace.filemanager.entity.DataDetail;
import com.traespace.filemanager.entity.FileRecord;
import com.traespace.filemanager.mapper.DataDetailMapper;
import com.traespace.filemanager.mapper.FileRecordMapper;
import com.traespace.filemanager.service.statistics.StatisticsService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * 统计服务实现
 *
 * @author Traespace
 * @since 2024-03-17
 */
@Service
public class StatisticsServiceImpl implements StatisticsService {

    private final FileRecordMapper fileRecordMapper;
    private final DataDetailMapper dataDetailMapper;

    public StatisticsServiceImpl(FileRecordMapper fileRecordMapper,
                                  DataDetailMapper dataDetailMapper) {
        this.fileRecordMapper = fileRecordMapper;
        this.dataDetailMapper = dataDetailMapper;
    }

    @Override
    public StatisticsResponse getStatistics(Long userId) {
        // 查询总文件数
        LambdaQueryWrapper<FileRecord> fileWrapper = new LambdaQueryWrapper<>();
        fileWrapper.eq(FileRecord::getUserId, userId)
                .eq(FileRecord::getStatus, 1);
        Long totalFiles = fileRecordMapper.selectCount(fileWrapper);

        // 查询用户的所有文件ID
        List<FileRecord> userFiles = fileRecordMapper.selectList(fileWrapper);
        long totalDataCount = 0L;
        if (!userFiles.isEmpty()) {
            List<Long> fileIds = userFiles.stream().map(FileRecord::getId).toList();
            LambdaQueryWrapper<DataDetail> dataWrapper = new LambdaQueryWrapper<>();
            dataWrapper.in(DataDetail::getFileId, fileIds);
            totalDataCount = dataDetailMapper.selectCount(dataWrapper);
        }

        // 查询今日上传文件数
        LocalDateTime todayStart = LocalDateTime.of(LocalDateTime.now().toLocalDate(), LocalTime.MIN);
        LocalDateTime todayEnd = LocalDateTime.of(LocalDateTime.now().toLocalDate(), LocalTime.MAX);

        LambdaQueryWrapper<FileRecord> todayFileWrapper = new LambdaQueryWrapper<>();
        todayFileWrapper.eq(FileRecord::getUserId, userId)
                .eq(FileRecord::getStatus, 1)
                .ge(FileRecord::getUploadTime, todayStart)
                .le(FileRecord::getUploadTime, todayEnd);
        Long todayUploadCount = fileRecordMapper.selectCount(todayFileWrapper);

        // 查询今日数据条数
        List<FileRecord> todayFiles = fileRecordMapper.selectList(todayFileWrapper);
        long todayDataCount = 0L;
        if (!todayFiles.isEmpty()) {
            List<Long> todayFileIds = todayFiles.stream().map(FileRecord::getId).toList();
            LambdaQueryWrapper<DataDetail> todayDataWrapper = new LambdaQueryWrapper<>();
            todayDataWrapper.in(DataDetail::getFileId, todayFileIds);
            todayDataCount = dataDetailMapper.selectCount(todayDataWrapper);
        }

        return new StatisticsResponse(totalFiles, totalDataCount, todayUploadCount, todayDataCount);
    }
}
