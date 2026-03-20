package com.traespace.filemanager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.traespace.filemanager.dto.request.common.BasePageRequest;
import com.traespace.filemanager.entity.OperationLog;
import com.traespace.filemanager.enums.OperationType;
import com.traespace.filemanager.mapper.OperationLogMapper;
import com.traespace.filemanager.service.log.OperationLogService;
import com.traespace.filemanager.vo.log.OperationLogItemVO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 操作日志服务实现
 *
 * @author Traespace
 * @since 2024-03-17
 */
@Service
public class OperationLogServiceImpl implements OperationLogService {

    private final OperationLogMapper operationLogMapper;

    public OperationLogServiceImpl(OperationLogMapper operationLogMapper) {
        this.operationLogMapper = operationLogMapper;
    }

    @Override
    public void saveLog(Long userId, String username, OperationType operationType, String description, String requestIp, Long costTime) {
        OperationLog log = new OperationLog();
        log.setUserId(userId);
        log.setUsername(username);
        log.setOperationType(operationType);
        log.setDescription(description);
        log.setRequestIp(requestIp);
        log.setCostTime(costTime);
        log.setCreateTime(LocalDateTime.now());
        operationLogMapper.insert(log);
    }

    @Override
    public IPage<OperationLogItemVO> getLogPage(Long userId, BasePageRequest request) {
        Page<OperationLog> page = new Page<>(request.getPage(), request.getSize());

        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<>();
        if (userId != null) {
            wrapper.eq(OperationLog::getUserId, userId);
        }
        wrapper.orderByDesc(OperationLog::getCreateTime);

        IPage<OperationLog> result = operationLogMapper.selectPage(page, wrapper);

        // 转换为VO
        Page<OperationLogItemVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        List<OperationLogItemVO> records = convertToVO(result.getRecords());
        voPage.setRecords(records);

        return voPage;
    }

    /**
     * 转换为VO列表
     */
    private List<OperationLogItemVO> convertToVO(List<OperationLog> logs) {
        List<OperationLogItemVO> result = new ArrayList<>();
        for (OperationLog log : logs) {
            OperationLogItemVO vo = new OperationLogItemVO();
            vo.setId(log.getId());
            vo.setUserId(log.getUserId());
            vo.setUsername(log.getUsername());
            vo.setOperationType(log.getOperationType());
            vo.setDescription(log.getDescription());
            vo.setRequestIp(log.getRequestIp());
            vo.setCreateTime(log.getCreateTime());
            vo.setCostTime(log.getCostTime());
            result.add(vo);
        }
        return result;
    }
}
