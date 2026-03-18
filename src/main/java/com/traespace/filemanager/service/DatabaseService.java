package com.traespace.filemanager.service;

/**
 * 数据库初始化服务
 *
 * @author Traespace
 * @since 2024-03-17
 */
public interface DatabaseService {

    /**
     * 初始化数据库表
     */
    void initDatabase();

    /**
     * 重置数据库（删除所有表并重新创建）
     */
    void resetDatabase();

    /**
     * 检查数据库是否已初始化
     */
    boolean isInitialized();
}
