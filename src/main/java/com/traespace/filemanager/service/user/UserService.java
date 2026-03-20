package com.traespace.filemanager.service.user;

import com.traespace.filemanager.dto.response.user.RoleResponse;
import com.traespace.filemanager.entity.User;
import com.traespace.filemanager.enums.UserRole;

/**
 * 用户服务接口
 *
 * @author Traespace
 * @since 2024-03-17
 */
public interface UserService {

    /**
     * 创建用户
     *
     * @param user 用户信息
     * @return 用户ID
     */
    Long createUser(User user);

    /**
     * 根据ID获取用户
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    User getUserById(Long userId);

    /**
     * 根据用户名获取用户
     *
     * @param username 用户名
     * @return 用户信息
     */
    User getUserByUsername(String username);

    /**
     * 更新用户信息
     *
     * @param user 用户信息
     */
    void updateUser(User user);

    /**
     * 获取当前用户角色
     *
     * @param userId 用户ID
     * @return 角色信息
     * @throws com.traespace.filemanager.exception.BizException 用户不存在时抛出
     */
    RoleResponse getCurrentRole(Long userId);

    /**
     * 更新用户角色
     *
     * @param userId 用户ID
     * @param role 角色
     * @throws com.traespace.filemanager.exception.BizException 用户不存在时抛出
     */
    void updateRole(Long userId, UserRole role);
}
