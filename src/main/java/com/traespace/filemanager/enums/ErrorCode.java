package com.traespace.filemanager.enums;

/**
 * 错误码枚举
 *
 * @author Traespace
 * @since 2024-03-17
 */
public enum ErrorCode {

    // ========== 通用错误 1xxx ==========
    SUCCESS(0, "success"),
    INVALID_PARAM(1001, "参数错误"),
    TOKEN_MISSING(1002, "Token缺失"),
    TOKEN_EXPIRED(1003, "Token已过期"),
    PERMISSION_DENIED(1004, "无权限"),

    // ========== 用户相关 2xxx ==========
    USER_EXISTS(2001, "用户名已存在"),
    USERNAME_EXISTS(2001, "用户名已存在"),
    USER_NOT_FOUND(2002, "用户不存在"),
    PASSWORD_ERROR(2003, "密码错误"),
    USER_DISABLED(2004, "用户已被禁用"),

    // ========== 文件相关 3xxx ==========
    FILE_FORMAT_ERROR(3001, "文件格式不支持"),
    FILE_SIZE_EXCEED(3002, "文件大小超限"),
    FILE_HEADER_MISMATCH(3003, "表头与模板不匹配"),
    FILE_NOT_FOUND(3004, "文件不存在"),

    // ========== 数据校验 4xxx ==========
    SEQ_NO_EMPTY(4001, "序号不能为空"),
    ID_CARD_ERROR(4002, "身份证格式错误"),
    PHONE_ERROR(4003, "手机号格式错误"),
    FIELD_REQUIRED(4004, "字段不能为空"),
    FIELD_TYPE_ERROR(4005, "字段格式错误"),
    ID_CARD_DUPLICATE(4006, "身份证号重复"),
    FIELD_COUNT_EXCEED(4007, "自定义字段数量超限"),
    FIELD_NAME_EXISTS(4008, "字段名称已存在"),

    // ========== 系统错误 5xxx ==========
    SYSTEM_ERROR(5001, "系统内部错误"),
    DB_ERROR(5002, "数据库错误");

    /**
     * 错误码
     */
    private final int code;

    /**
     * 错误信息
     */
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
