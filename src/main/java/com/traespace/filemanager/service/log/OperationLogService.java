package com.traespace.filemanager.service.log;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.traespace.filemanager.dto.request.common.BasePageRequest;
import com.traespace.filemanager.enums.OperationType;
import com.traespace.filemanager.vo.log.OperationLogItemVO;

/**
 * 操作日志服务接口
 *
 * @author Traespace
 * @since 2024-03-17
 */
public interface OperationLogService {

    /**
     * 保存操作日志
     *
     * @param userId         用户ID
     * @param username       用户名
     * @param operationType  操作类型
     * @param description    操作描述
     * @param requestIp      请求IP
     * @param costTime       耗时（毫秒）
     */
    void saveLog(Long userId, String username, OperationType operationType, String description, String requestIp, Long costTime);

    /**
     * 获取操作日志分页
     *
     * @param userId  用户ID（null表示查询所有用户）
     * @param request 分页请求
     * @return 分页结果
     */
    IPage<OperationLogItemVO> getLogPage(Long userId, BasePageRequest request);
}
