package com.traespace.filemanager.service.user;

import com.traespace.filemanager.dto.response.user.RoleResponse;
import com.traespace.filemanager.entity.User;
import com.traespace.filemanager.enums.ErrorCode;
import com.traespace.filemanager.enums.UserRole;
import com.traespace.filemanager.exception.BizException;
import com.traespace.filemanager.mapper.UserMapper;
import com.traespace.filemanager.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * UserService测试
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void testCreateUser() {
        // 测试创建用户
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");
        user.setRole(UserRole.USER);

        when(userMapper.insert(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(1001L);
            return 1;
        });

        Long userId = userService.createUser(user);

        assertThat(userId).isNotNull();
        assertThat(userId).isEqualTo(1001L);
        verify(userMapper).insert(user);
    }

    @Test
    void testGetUserById() {
        // 测试根据ID获取用户
        User user = new User();
        user.setId(1001L);
        user.setUsername("testuser");
        user.setRole(UserRole.USER);

        when(userMapper.selectById(1001L)).thenReturn(user);

        User result = userService.getUserById(1001L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1001L);
        assertThat(result.getUsername()).isEqualTo("testuser");
    }

    @Test
    void testGetUserByUsername() {
        // 测试根据用户名获取用户
        User user = new User();
        user.setId(1001L);
        user.setUsername("testuser");
        user.setRole(UserRole.USER);

        when(userMapper.selectOne(any())).thenReturn(user);

        User result = userService.getUserByUsername("testuser");

        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("testuser");
    }

    // ========== 角色相关测试 ==========

    @Test
    void testGetCurrentRole() {
        // 测试获取当前用户角色
        User user = new User();
        user.setId(1001L);
        user.setUsername("testuser");
        user.setRole(UserRole.USER);

        when(userMapper.selectById(1001L)).thenReturn(user);

        RoleResponse response = userService.getCurrentRole(1001L);

        assertThat(response).isNotNull();
        assertThat(response.getRole()).isEqualTo(UserRole.USER);
        assertThat(response.getRoleDesc()).isEqualTo("普通用户");
        verify(userMapper).selectById(1001L);
    }

    @Test
    void testGetCurrentRole_Admin() {
        // 测试获取管理员角色
        User user = new User();
        user.setId(1001L);
        user.setUsername("admin");
        user.setRole(UserRole.ADMIN);

        when(userMapper.selectById(1001L)).thenReturn(user);

        RoleResponse response = userService.getCurrentRole(1001L);

        assertThat(response).isNotNull();
        assertThat(response.getRole()).isEqualTo(UserRole.ADMIN);
        assertThat(response.getRoleDesc()).isEqualTo("管理员");
    }

    @Test
    void testGetCurrentRole_UserNotFound() {
        // 测试获取不存在的用户角色
        when(userMapper.selectById(999L)).thenReturn(null);

        assertThatThrownBy(() -> userService.getCurrentRole(999L))
            .isInstanceOf(BizException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND);

        verify(userMapper).selectById(999L);
    }

    @Test
    void testUpdateRole() {
        // 测试更新用户角色
        User user = new User();
        user.setId(1001L);
        user.setUsername("testuser");
        user.setRole(UserRole.USER);

        when(userMapper.selectById(1001L)).thenReturn(user);
        when(userMapper.updateById(any(User.class))).thenReturn(1);

        userService.updateRole(1001L, UserRole.ADMIN);

        verify(userMapper).selectById(1001L);
        verify(userMapper).updateById(any(User.class));
    }

    @Test
    void testUpdateRole_UserNotFound() {
        // 测试更新不存在的用户角色
        when(userMapper.selectById(999L)).thenReturn(null);

        assertThatThrownBy(() -> userService.updateRole(999L, UserRole.ADMIN))
            .isInstanceOf(BizException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND);

        verify(userMapper).selectById(999L);
        verify(userMapper, never()).updateById(any());
    }
}
