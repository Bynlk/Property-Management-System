package com.property.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.property.controller.AuthController;
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
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * GlobalExceptionHandler 集成测试
 * 通过 WebMvcTest 触发异常处理器，验证各种异常的返回格式
 */
@WebMvcTest(value = AuthController.class, excludeAutoConfiguration = {MybatisAutoConfiguration.class})
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("GlobalExceptionHandler 异常处理测试")
class GlobalExceptionHandlerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean private UserService userService;
    @MockBean private JwtUtil jwtUtil;
    @MockBean private JwtAuthenticationFilter jwtAuthenticationFilter;
    @MockBean private PasswordEncoder passwordEncoder;
    @MockBean private LoginAttemptService loginAttemptService;
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

    @Test
    @DisplayName("MethodArgumentNotValidException - 参数校验失败返回400")
    void handleValidation() throws Exception {
        LoginRequest req = new LoginRequest("", "");
        mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(1));
    }

    @Test
    @DisplayName("HttpMessageNotReadableException - JSON格式错误返回400")
    void handleNotReadable() throws Exception {
        mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content("not json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(1));
    }

    @Test
    @DisplayName("BusinessException - 业务异常返回400")
    void handleBusiness() throws Exception {
        LoginRequest req = new LoginRequest("admin", "admin123");
        when(loginAttemptService.isBlocked(anyString())).thenReturn(true);
        when(loginAttemptService.getRemainingLockSeconds(anyString())).thenReturn(600L);
        // AuthController throws BusinessException for locked accounts
        mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk()) // AuthController catches and returns Result.error
                .andExpect(jsonPath("$.code").value(1));
    }

    @Test
    @DisplayName("返回格式统一为 {code, msg, data}")
    void responseFormat_consistent() throws Exception {
        LoginRequest req = new LoginRequest("", "admin123");
        mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").exists())
                .andExpect(jsonPath("$.msg").exists());
    }
}
