package com.traespace.filemanager.vo.log;

import com.traespace.filemanager.enums.OperationType;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * OperationLogItemVO测试
 */
class OperationLogItemVOTest {

    @Test
    void testOperationLogItemVO() {
        // 测试日志项VO
        OperationLogItemVO vo = new OperationLogItemVO();
        vo.setId(1L);
        vo.setUserId(1001L);
        vo.setUsername("testuser");
        vo.setOperationType(OperationType.UPLOAD);
        vo.setDescription("上传文件");
        vo.setRequestIp("127.0.0.1");
        vo.setCreateTime(LocalDateTime.now());

        assertThat(vo.getId()).isEqualTo(1L);
        assertThat(vo.getUserId()).isEqualTo(1001L);
        assertThat(vo.getUsername()).isEqualTo("testuser");
        assertThat(vo.getOperationType()).isEqualTo(OperationType.UPLOAD);
        assertThat(vo.getDescription()).isEqualTo("上传文件");
        assertThat(vo.getRequestIp()).isEqualTo("127.0.0.1");
        assertThat(vo.getCreateTime()).isNotNull();
    }

    @Test
    void testBuilderPattern() {
        // 测试链式调用
        LocalDateTime now = LocalDateTime.now();
        OperationLogItemVO vo = new OperationLogItemVO()
                .setId(1L)
                .setUserId(1001L)
                .setUsername("testuser")
                .setOperationType(OperationType.DOWNLOAD)
                .setDescription("下载文件")
                .setRequestIp("192.168.1.1")
                .setCreateTime(now);

        assertThat(vo.getId()).isEqualTo(1L);
        assertThat(vo.getUsername()).isEqualTo("testuser");
        assertThat(vo.getOperationType()).isEqualTo(OperationType.DOWNLOAD);
        assertThat(vo.getDescription()).isEqualTo("下载文件");
        assertThat(vo.getRequestIp()).isEqualTo("192.168.1.1");
        assertThat(vo.getCreateTime()).isEqualTo(now);
    }

    @Test
    void testAllOperationTypes() {
        // 测试所有操作类型
        OperationLogItemVO vo = new OperationLogItemVO();

        vo.setOperationType(OperationType.UPLOAD);
        assertThat(vo.getOperationType()).isEqualTo(OperationType.UPLOAD);

        vo.setOperationType(OperationType.DOWNLOAD);
        assertThat(vo.getOperationType()).isEqualTo(OperationType.DOWNLOAD);

        vo.setOperationType(OperationType.DELETE);
        assertThat(vo.getOperationType()).isEqualTo(OperationType.DELETE);

        vo.setOperationType(OperationType.QUERY);
        assertThat(vo.getOperationType()).isEqualTo(OperationType.QUERY);

        vo.setOperationType(OperationType.OTHER);
        assertThat(vo.getOperationType()).isEqualTo(OperationType.OTHER);
    }
}
