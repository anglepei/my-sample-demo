package com.traespace.filemanager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.traespace.filemanager.entity.OperationLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 操作日志Mapper
 *
 * @author Traespace
 * @since 2024-03-17
 */
@Mapper
public interface OperationLogMapper extends BaseMapper<OperationLog> {
}
