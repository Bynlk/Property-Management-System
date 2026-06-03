package com.property.controller;

import com.property.mapper.*;
import com.property.security.JwtAuthenticationFilter;
import com.property.security.JwtUtil;
import com.property.security.LoginAttemptService;
import com.property.service.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = DashboardController.class, excludeAutoConfiguration = {MybatisAutoConfiguration.class})
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("DashboardController 集成测试")
class DashboardControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockBean private OwnerService ownerService;
    @MockBean private HouseService houseService;
    @MockBean private FeeService feeService;
    @MockBean private ComplaintService complaintService;
    @MockBean private RepairService repairService;
    @MockBean private JwtUtil jwtUtil;
    @MockBean private JwtAuthenticationFilter jwtAuthenticationFilter;
    @MockBean private LoginAttemptService loginAttemptService;
    @MockBean private PasswordEncoder passwordEncoder;
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
    @DisplayName("GET /api/dashboard/stats - 返回正确统计数据")
    void stats_returnsCorrectCounts() throws Exception {
        when(ownerService.count()).thenReturn(10);
        when(houseService.count()).thenReturn(50);
        when(feeService.count()).thenReturn(100);
        when(complaintService.count()).thenReturn(5);
        when(repairService.count()).thenReturn(8);

        mockMvc.perform(get("/api/dashboard/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.owners").value(10))
                .andExpect(jsonPath("$.data.houses").value(50))
                .andExpect(jsonPath("$.data.fees").value(100))
                .andExpect(jsonPath("$.data.complaints").value(5))
                .andExpect(jsonPath("$.data.repairs").value(8));
    }

    @Test
    @DisplayName("GET /api/dashboard/stats - 全部为0")
    void stats_allZero() throws Exception {
        when(ownerService.count()).thenReturn(0);
        when(houseService.count()).thenReturn(0);
        when(feeService.count()).thenReturn(0);
        when(complaintService.count()).thenReturn(0);
        when(repairService.count()).thenReturn(0);

        mockMvc.perform(get("/api/dashboard/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.owners").value(0))
                .andExpect(jsonPath("$.data.houses").value(0));
    }
}
