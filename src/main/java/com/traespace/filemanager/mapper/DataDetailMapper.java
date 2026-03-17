package com.traespace.filemanager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.traespace.filemanager.entity.DataDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 数据明细Mapper
 *
 * @author Traespace
 * @since 2024-03-17
 */
@Mapper
public interface DataDetailMapper extends BaseMapper<DataDetail> {

    /**
     * 根据文件ID查询数据详情
     */
    @Select("SELECT id, file_id, seq_no, id_card, phone, " +
            "custom_fields, row_num, create_time " +
            "FROM t_data_detail " +
            "WHERE file_id = #{fileId} " +
            "ORDER BY row_num " +
            "LIMIT #{offset}, #{size}")
    List<DataDetail> selectByFileId(@Param("fileId") Long fileId,
                                    @Param("offset") Integer offset,
                                    @Param("size") Integer size);

    /**
     * 统计文件数据条数
     */
    @Select("SELECT COUNT(*) FROM t_data_detail WHERE file_id = #{fileId}")
    Integer countByFileId(@Param("fileId") Long fileId);

    /**
     * 检查身份证重复（同一文件内）
     */
    @Select("SELECT COUNT(*) FROM t_data_detail " +
            "WHERE file_id = #{fileId} AND id_card = #{idCard}")
    Integer countByIdCard(@Param("fileId") Long fileId,
                         @Param("idCard") String idCard);
}
