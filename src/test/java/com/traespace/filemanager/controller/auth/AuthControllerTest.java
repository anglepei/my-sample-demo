package com.traespace.filemanager.controller.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.traespace.filemanager.dto.request.auth.LoginRequest;
import com.traespace.filemanager.dto.request.auth.RegisterRequest;
import com.traespace.filemanager.dto.response.auth.LoginResponse;
import com.traespace.filemanager.enums.UserRole;
import com.traespace.filemanager.service.auth.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * AuthController测试
 */
@WebMvcTest(controllers = AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @Test
    void testRegister() throws Exception {
        // 测试注册接口
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setPassword("Password123");
        request.setRole("USER");

        when(authService.register(any(RegisterRequest.class))).thenReturn(1001L);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data").value(1001L));
    }

    @Test
    void testLogin() throws Exception {
        // 测试登录接口
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("Password123");

        LoginResponse response = new LoginResponse("test-token", 1001L, "testuser", UserRole.USER);
        when(authService.login(any(LoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.token").value("test-token"))
                .andExpect(jsonPath("$.data.username").value("testuser"));
    }

    @Test
    void testLogout() throws Exception {
        // 测试登出接口
        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }
}
