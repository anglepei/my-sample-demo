package com.traespace.filemanager.service.impl;

import com.traespace.filemanager.service.DatabaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

/**
 * 数据库初始化服务实现
 *
 * @author Traespace
 * @since 2024-03-17
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DatabaseServiceImpl implements DatabaseService {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void initDatabase() {
        log.info("[Database] 开始初始化数据库表");

        try {
            // 创建用户表
            createUserTable();

            // 创建字段配置表
            createFieldConfigTable();

            // 创建文件记录表
            createFileRecordTable();

            // 创建数据明细表
            createDataDetailTable();

            // 创建操作日志表
            createOperationLogTable();

            // 插入默认管理员
            insertDefaultAdmin();

            log.info("[Database] 数据库初始化完成");
        } catch (Exception e) {
            log.error("[Database] 数据库初始化失败", e);
            throw new RuntimeException("数据库初始化失败: " + e.getMessage());
        }
    }

    @Override
    public void resetDatabase() {
        log.info("[Database] 开始重置数据库");

        try {
            // 删除表
            dropTableIfExists("t_data_detail");
            dropTableIfExists("t_file_record");
            dropTableIfExists("t_field_config");
            dropTableIfExists("t_operation_log");
            dropTableIfExists("t_user");

            // 重新创建
            initDatabase();

            log.info("[Database] 数据库重置完成");
        } catch (Exception e) {
            log.error("[Database] 数据库重置失败", e);
            throw new RuntimeException("数据库重置失败: " + e.getMessage());
        }
    }

    @Override
    public boolean isInitialized() {
        try {
            Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 't_user'",
                Integer.class
            );
            return count != null && count > 0;
        } catch (Exception e) {
            log.error("[Database] 检查数据库状态失败", e);
            return false;
        }
    }

    private void dropTableIfExists(String tableName) {
        try {
            jdbcTemplate.execute("DROP TABLE " + tableName);
            log.info("[Database] 删除表: {}", tableName);
        } catch (Exception e) {
            // 表不存在时继续执行
            log.debug("[Database] 删除表失败（可能不存在）: {}", tableName);
        }
    }

    private void createUserTable() {
        // 先删除表（如果存在）
        dropTableIfExists("t_user");

        String sql = """
            CREATE TABLE t_user (
                id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
                username VARCHAR(64) NOT NULL UNIQUE COMMENT '用户名',
                password VARCHAR(128) NOT NULL COMMENT '密码（加密）',
                role VARCHAR(16) NOT NULL DEFAULT 'USER' COMMENT '角色：USER/ADMIN',
                status INT NOT NULL DEFAULT 1 COMMENT '状态（1:正常 0:禁用）',
                create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                INDEX idx_username (username)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表'
            """;
        jdbcTemplate.execute(sql);
        log.info("[Database] 创建表: t_user");
    }

    private void createFieldConfigTable() {
        // 先删除表（如果存在）
        dropTableIfExists("t_field_config");

        String sql = """
            CREATE TABLE t_field_config (
                id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
                user_id BIGINT NOT NULL COMMENT '用户ID',
                field_name VARCHAR(64) NOT NULL COMMENT '字段名称',
                field_type VARCHAR(16) NOT NULL COMMENT '字段类型：TEXT/NUMBER/DATE',
                required TINYINT NOT NULL DEFAULT 0 COMMENT '是否必填：0否1是',
                sort_order INT NOT NULL COMMENT '排序顺序',
                create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                INDEX idx_user_id (user_id)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字段配置表'
            """;
        jdbcTemplate.execute(sql);
        log.info("[Database] 创建表: t_field_config");
    }

    private void createFileRecordTable() {
        // 先删除表（如果存在）
        dropTableIfExists("t_file_record");

        String sql = """
            CREATE TABLE t_file_record (
                id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
                file_name VARCHAR(255) NOT NULL COMMENT '文件名（系统生成）',
                original_name VARCHAR(255) NOT NULL COMMENT '原始文件名',
                user_id BIGINT NOT NULL COMMENT '上传人ID',
                file_type VARCHAR(16) NOT NULL COMMENT '文件类型：XLSX/XLS/CSV',
                status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1正常0已删除',
                row_count INT NOT NULL COMMENT '数据条数',
                field_config_snapshot JSON COMMENT '字段配置快照',
                upload_time DATETIME NOT NULL COMMENT '上传时间',
                create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                INDEX idx_user_id (user_id),
                INDEX idx_upload_time (upload_time)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文件记录表'
            """;
        jdbcTemplate.execute(sql);
        log.info("[Database] 创建表: t_file_record");
    }

    private void createDataDetailTable() {
        // 先删除表（如果存在）
        dropTableIfExists("t_data_detail");

        String sql = """
            CREATE TABLE t_data_detail (
                id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
                file_id BIGINT NOT NULL COMMENT '文件ID',
                seq_no VARCHAR(32) NOT NULL COMMENT '序号',
                id_card VARCHAR(32) NOT NULL COMMENT '身份证号',
                phone VARCHAR(16) NOT NULL COMMENT '手机号',
                custom_fields JSON NOT NULL COMMENT '自定义字段JSON',
                row_num INT NOT NULL COMMENT '原文件行号',
                create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                INDEX idx_file_id (file_id),
                INDEX idx_id_card (id_card)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据明细表'
            """;
        jdbcTemplate.execute(sql);
        log.info("[Database] 创建表: t_data_detail");
    }

    private void createOperationLogTable() {
        // 先删除表（如果存在）
        dropTableIfExists("t_operation_log");

        String sql = """
            CREATE TABLE t_operation_log (
                id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
                user_id BIGINT NOT NULL COMMENT '用户ID',
                username VARCHAR(64) NOT NULL COMMENT '用户名',
                operation_type VARCHAR(32) NOT NULL COMMENT '操作类型：UPLOAD/DELETE等',
                description VARCHAR(500) COMMENT '操作描述',
                request_ip VARCHAR(64) COMMENT '请求IP',
                target_id BIGINT COMMENT '目标ID（文件ID）',
                detail VARCHAR(500) COMMENT '操作详情（保留）',
                cost_time BIGINT COMMENT '耗时（毫秒）',
                create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                INDEX idx_user_id (user_id),
                INDEX idx_create_time (create_time)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表'
            """;
        jdbcTemplate.execute(sql);
        log.info("[Database] 创建表: t_operation_log");
    }

    private void insertDefaultAdmin() {
        // 检查是否已有管理员
        Integer count = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM t_user WHERE username = 'admin'",
            Integer.class
        );

        if (count == null || count == 0) {
            // admin123 的BCrypt加密结果
            String password = "$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z2EHdHmAjQJtQ2uxR.7P0J9G";

            jdbcTemplate.update(
                "INSERT INTO t_user (username, password, role) VALUES (?, ?, 'ADMIN')",
                "admin", password
            );
            log.info("[Database] 插入默认管理员账号: admin/admin123");
        }
    }
}
