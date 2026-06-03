package com.property.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.property.dto.LoginRequest;
import com.property.entity.User;
import com.property.enums.UserRole;
import com.property.mapper.*;
import com.property.security.JwtAuthenticationFilter;
import com.property.security.JwtUtil;
import com.property.security.LoginAttemptService;
import com.property.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = AuthController.class, excludeAutoConfiguration = {MybatisAutoConfiguration.class})
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("AuthController 集成测试")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private LoginAttemptService loginAttemptService;

    // Mock all MyBatis mappers to prevent SqlSessionFactory requirement
    @MockBean private OwnerMapper ownerMapper;
    @MockBean private HouseMapper houseMapper;
    @MockBean private FeeMapper feeMapper;
    @MockBean private ComplaintMapper complaintMapper;
    @MockBean private RepairMapper repairMapper;
    @MockBean private EmployeeMapper employeeMapper;
    @MockBean private ParkingMapper parkingMapper;
    @MockBean private DutyMapper dutyMapper;
    @MockBean private UserMapper userMapper;
    @MockBean private StatusChangeLogMapper statusChangeLogMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("登录成功返回token")
    void login_success() throws Exception {
        LoginRequest request = new LoginRequest("admin", "admin123");
        User user = User.builder()
                .id(1)
                .username("admin")
                .realName("系统管理员")
                .role(UserRole.ADMIN)
                .tokenVersion(0)
                .password("$2a$10$encoded")
                .build();

        when(userService.getByUsername("admin")).thenReturn(user);
        when(passwordEncoder.matches("admin123", "$2a$10$encoded")).thenReturn(true);
        when(jwtUtil.generateToken("admin", "admin", 0)).thenReturn("mock-jwt-token");
        when(loginAttemptService.isBlocked(anyString())).thenReturn(false);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.msg").value("登录成功"))
                .andExpect(jsonPath("$.data.token").value("mock-jwt-token"))
                .andExpect(jsonPath("$.data.user.username").value("admin"))
                .andExpect(jsonPath("$.data.user.role").value("admin"));

        verify(loginAttemptService).loginSucceeded(anyString());
    }

    @Test
    @DisplayName("登录失败-用户名不存在")
    void login_fail_userNotFound() throws Exception {
        LoginRequest request = new LoginRequest("nobody", "password");
        when(userService.getByUsername("nobody")).thenReturn(null);
        when(loginAttemptService.isBlocked(anyString())).thenReturn(false);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.msg").value("用户名或密码错误"));

        verify(loginAttemptService).loginFailed(anyString());
    }

    @Test
    @DisplayName("登录失败-密码错误")
    void login_fail_wrongPassword() throws Exception {
        LoginRequest request = new LoginRequest("admin", "wrongpassword");
        User user = User.builder()
                .id(1)
                .username("admin")
                .password("$2a$10$encoded")
                .role(UserRole.ADMIN)
                .tokenVersion(0)
                .build();

        when(userService.getByUsername("admin")).thenReturn(user);
        when(passwordEncoder.matches("wrongpassword", "$2a$10$encoded")).thenReturn(false);
        when(loginAttemptService.isBlocked(anyString())).thenReturn(false);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.msg").value("用户名或密码错误"));

        verify(loginAttemptService).loginFailed(anyString());
        verify(jwtUtil, never()).generateToken(anyString(), anyString(), eq(0));
    }

    @Test
    @DisplayName("登录被限流锁定")
    void login_blocked() throws Exception {
        LoginRequest request = new LoginRequest("admin", "admin123");
        when(loginAttemptService.isBlocked(anyString())).thenReturn(true);
        when(loginAttemptService.getRemainingLockSeconds(anyString())).thenReturn(600L);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.msg", containsString("锁定")));

        verify(userService, never()).getByUsername(anyString());
    }

    @Test
    @DisplayName("登录请求参数校验-空用户名")
    void login_validation_emptyUsername() throws Exception {
        LoginRequest request = new LoginRequest("", "admin123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("登录请求参数校验-空密码")
    void login_validation_emptyPassword() throws Exception {
        LoginRequest request = new LoginRequest("admin", "");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
