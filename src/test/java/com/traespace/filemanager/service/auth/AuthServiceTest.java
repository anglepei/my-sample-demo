package com.traespace.filemanager.service.auth;

import com.traespace.filemanager.dto.request.auth.LoginRequest;
import com.traespace.filemanager.dto.request.auth.RegisterRequest;
import com.traespace.filemanager.dto.response.auth.LoginResponse;
import com.traespace.filemanager.entity.User;
import com.traespace.filemanager.enums.ErrorCode;
import com.traespace.filemanager.enums.UserRole;
import com.traespace.filemanager.exception.BizException;
import com.traespace.filemanager.mapper.UserMapper;
import com.traespace.filemanager.service.impl.AuthServiceImpl;
import com.traespace.filemanager.service.user.UserService;
import com.traespace.filemanager.util.PasswordUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * AuthService测试
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthServiceImpl authService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1001L);
        testUser.setUsername("testuser");
        testUser.setPassword(PasswordUtil.encode("Password123"));
        testUser.setRole(UserRole.USER);
        testUser.setStatus(1);
    }

    @Test
    void testRegisterSuccess() {
        // 测试注册成功
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newuser");
        request.setPassword("Password123");
        request.setRole("USER");

        when(userMapper.selectByUsername("newuser")).thenReturn(null);
        when(userMapper.insert(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1002L);
            return 1;
        });

        Long userId = authService.register(request);

        assertThat(userId).isNotNull();
        verify(userMapper).insert(any(User.class));
    }

    @Test
    void testRegisterDuplicateUsername() {
        // 测试注册用户名重复
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setPassword("Password123");
        request.setRole("USER");

        when(userMapper.selectByUsername("testuser")).thenReturn(testUser);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(BizException.class);
    }

    @Test
    void testLoginSuccess() {
        // 测试登录成功
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("Password123");

        when(userMapper.selectByUsername("testuser")).thenReturn(testUser);

        LoginResponse response = authService.login(request);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isNotEmpty();
        assertThat(response.getUserId()).isEqualTo(1001L);
        assertThat(response.getUsername()).isEqualTo("testuser");
    }

    @Test
    void testLoginUserNotFound() {
        // 测试登录用户不存在
        LoginRequest request = new LoginRequest();
        request.setUsername("unknown");
        request.setPassword("Password123");

        when(userMapper.selectByUsername("unknown")).thenReturn(null);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BizException.class);
    }

    @Test
    void testLoginWrongPassword() {
        // 测试登录密码错误
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("WrongPassword");

        when(userMapper.selectByUsername("testuser")).thenReturn(testUser);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BizException.class);
    }
}
