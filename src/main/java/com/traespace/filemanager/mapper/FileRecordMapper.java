package com.traespace.filemanager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.traespace.filemanager.entity.FileRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文件记录Mapper
 *
 * @author Traespace
 * @since 2024-03-17
 */
@Mapper
public interface FileRecordMapper extends BaseMapper<FileRecord> {
}
