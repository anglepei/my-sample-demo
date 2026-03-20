package com.traespace.filemanager.controller.user;

import com.traespace.filemanager.config.UserContext;
import com.traespace.filemanager.dto.request.user.RoleUpdateRequest;
import com.traespace.filemanager.dto.response.common.Result;
import com.traespace.filemanager.dto.response.user.RoleResponse;
import com.traespace.filemanager.enums.UserRole;
import com.traespace.filemanager.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * UserController测试
 */
@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void testControllerExists() {
        // 测试控制器类存在
        assertThat(UserController.class).isNotNull();
    }

    // ========== 角色相关测试 ==========

    @Test
    void testGetCurrentRole() throws Exception {
        // 测试获取当前用户角色
        RoleResponse roleResponse = new RoleResponse();
        roleResponse.setRole(UserRole.USER);
        roleResponse.setRoleDesc("普通用户");

        try (MockedStatic<UserContext> mockedContext = mockStatic(UserContext.class)) {
            mockedContext.when(UserContext::getUserId).thenReturn(1001L);
            when(userService.getCurrentRole(1001L)).thenReturn(roleResponse);

            mockMvc.perform(get("/api/user/role"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(0))
                    .andExpect(jsonPath("$.data.role").value("USER"))
                    .andExpect(jsonPath("$.data.roleDesc").value("普通用户"));
        }
    }

    @Test
    void testGetCurrentRole_Admin() throws Exception {
        // 测试获取管理员角色
        RoleResponse roleResponse = new RoleResponse();
        roleResponse.setRole(UserRole.ADMIN);
        roleResponse.setRoleDesc("管理员");

        try (MockedStatic<UserContext> mockedContext = mockStatic(UserContext.class)) {
            mockedContext.when(UserContext::getUserId).thenReturn(1001L);
            when(userService.getCurrentRole(1001L)).thenReturn(roleResponse);

            mockMvc.perform(get("/api/user/role"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.role").value("ADMIN"))
                    .andExpect(jsonPath("$.data.roleDesc").value("管理员"));
        }
    }

    @Test
    void testUpdateRole() throws Exception {
        // 测试更新用户角色
        try (MockedStatic<UserContext> mockedContext = mockStatic(UserContext.class)) {
            mockedContext.when(UserContext::getUserId).thenReturn(1001L);
            doNothing().when(userService).updateRole(eq(1001L), eq(UserRole.ADMIN));

            mockMvc.perform(post("/api/user/role")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"role\":\"ADMIN\"}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(0));
        }
    }

    @Test
    void testUpdateRole_ToUser() throws Exception {
        // 测试更新为普通用户
        try (MockedStatic<UserContext> mockedContext = mockStatic(UserContext.class)) {
            mockedContext.when(UserContext::getUserId).thenReturn(1001L);
            doNothing().when(userService).updateRole(eq(1001L), eq(UserRole.USER));

            mockMvc.perform(post("/api/user/role")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"role\":\"USER\"}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(0));
        }
    }

    @Test
    void testUpdateRole_InvalidRole() throws Exception {
        // 测试无效角色参数
        mockMvc.perform(post("/api/user/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"role\":\"INVALID\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateRole_EmptyRole() throws Exception {
        // 测试空角色参数
        mockMvc.perform(post("/api/user/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"role\":\"\"}"))
                .andExpect(status().isBadRequest());
    }
}
