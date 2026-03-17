package com.traespace.filemanager.entity;

import com.traespace.filemanager.enums.OperationType;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

/**
 * OperationLog实体测试
 */
class OperationLogTest {

    @Test
    void testOperationLogProperties() {
        // 测试OperationLog实体属性
        OperationLog log = new OperationLog();
        log.setId(1L);
        log.setUserId(100L);
        log.setUsername("testuser");
        log.setOperationType(OperationType.UPLOAD);
        log.setDescription("上传文件 test.xlsx");
        log.setRequestIp("127.0.0.1");
        log.setTargetId(200L);
        log.setCostTime(1000L);
        log.setCreateTime(LocalDateTime.now());

        assertThat(log.getId()).isEqualTo(1L);
        assertThat(log.getUserId()).isEqualTo(100L);
        assertThat(log.getUsername()).isEqualTo("testuser");
        assertThat(log.getOperationType()).isEqualTo(OperationType.UPLOAD);
        assertThat(log.getDescription()).isEqualTo("上传文件 test.xlsx");
        assertThat(log.getRequestIp()).isEqualTo("127.0.0.1");
        assertThat(log.getTargetId()).isEqualTo(200L);
        assertThat(log.getCostTime()).isEqualTo(1000L);
        assertThat(log.getCreateTime()).isNotNull();
    }

    @Test
    void testOperationTypeEnum() {
        // 测试操作类型枚举
        OperationLog log = new OperationLog();
        log.setOperationType(OperationType.DELETE);
        assertThat(log.getOperationType()).isEqualTo(OperationType.DELETE);
        assertThat(log.getOperationType().getDescription()).isEqualTo("删除");
    }
}
