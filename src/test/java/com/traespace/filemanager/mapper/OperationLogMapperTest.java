package com.traespace.filemanager.mapper;

import com.traespace.filemanager.entity.OperationLog;
import com.traespace.filemanager.enums.OperationType;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * OperationLogMapper测试
 */
class OperationLogMapperTest {

    @Test
    void testOperationLogMapperInterfaceExists() {
        // 测试Mapper接口存在
        assertThat(OperationLogMapper.class).isNotNull();
    }

    @Test
    void testOperationLogEntityExists() {
        // 测试OperationLog实体存在
        OperationLog log = new OperationLog();
        assertThat(log).isNotNull();
    }

    @Test
    void testOperationLogProperties() {
        // 测试OperationLog属性设置
        OperationLog log = new OperationLog();
        log.setId(1L);
        log.setUserId(1001L);
        log.setUsername("testuser");
        log.setOperationType(OperationType.UPLOAD);
        log.setDescription("上传文件");
        log.setRequestIp("127.0.0.1");
        log.setCreateTime(LocalDateTime.now());

        assertThat(log.getId()).isEqualTo(1L);
        assertThat(log.getUserId()).isEqualTo(1001L);
        assertThat(log.getUsername()).isEqualTo("testuser");
        assertThat(log.getOperationType()).isEqualTo(OperationType.UPLOAD);
        assertThat(log.getDescription()).isEqualTo("上传文件");
        assertThat(log.getRequestIp()).isEqualTo("127.0.0.1");
        assertThat(log.getCreateTime()).isNotNull();
    }
}
