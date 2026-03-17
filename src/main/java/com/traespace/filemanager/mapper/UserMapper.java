package com.traespace.filemanager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.traespace.filemanager.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 用户Mapper
 *
 * @author Traespace
 * @since 2024-03-17
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户信息
     */
    @Select("SELECT * FROM t_user WHERE username = #{username}")
    User selectByUsername(String username);
}
