-- 文件上传下载校验管理系统 - 建表SQL

-- 用户表
CREATE TABLE IF NOT EXISTS t_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    username VARCHAR(64) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(128) NOT NULL COMMENT '密码（加密）',
    role VARCHAR(16) NOT NULL DEFAULT 'USER' COMMENT '角色：USER/ADMIN',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 字段配置表
CREATE TABLE IF NOT EXISTS t_field_config (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    field_name VARCHAR(64) NOT NULL COMMENT '字段名称',
    field_type VARCHAR(16) NOT NULL COMMENT '字段类型：TEXT/NUMBER/DATE',
    required TINYINT NOT NULL DEFAULT 0 COMMENT '是否必填：0否1是',
    sort_order INT NOT NULL COMMENT '排序顺序',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字段配置表';

-- 文件记录表
CREATE TABLE IF NOT EXISTS t_file_record (
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文件记录表';

-- 数据明细表
CREATE TABLE IF NOT EXISTS t_data_detail (
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据明细表';

-- 操作日志表
CREATE TABLE IF NOT EXISTS t_operation_log (
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';
