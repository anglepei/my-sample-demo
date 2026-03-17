package com.traespace.filemanager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.traespace.filemanager.entity.User;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * UserMapper测试
 */
class UserMapperTest {

    @Test
    void testMapperInterface() {
        // 测试Mapper接口存在
        assertThat(UserMapper.class).isNotNull();
    }

    @Test
    void testMapperExtendsBaseMapper() {
        // 测试Mapper继承了BaseMapper
        assertThat(BaseMapper.class.isAssignableFrom(UserMapper.class)).isTrue();
    }
}
