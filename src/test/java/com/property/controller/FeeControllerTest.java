package com.property.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.property.common.BusinessException;
import com.property.common.PageResult;
import com.property.dto.FeeCreateRequest;
import com.property.dto.FeeUpdateRequest;
import com.property.entity.Fee;
import com.property.enums.FeeStatus;
import com.property.enums.FeeType;
import com.property.mapper.*;
import com.property.security.JwtAuthenticationFilter;
import com.property.security.JwtUtil;
import com.property.security.LoginAttemptService;
import com.property.service.FeeService;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = FeeController.class, excludeAutoConfiguration = {MybatisAutoConfiguration.class})
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("FeeController 集成测试")
class FeeControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean private FeeService feeService;
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
    @DisplayName("GET /api/fee - 分页查询")
    void getPage() throws Exception {
        when(feeService.getByPage(any(), anyString(), anyString(), anyInt(), anyInt()))
                .thenReturn(PageResult.of(List.of(), 0, 1, 10));
        mockMvc.perform(get("/api/fee")).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.total").value(0));
    }

    @Test
    @DisplayName("GET /api/fee/{id} - 查询存在")
    void getById_exists() throws Exception {
        when(feeService.getById(1)).thenReturn(Fee.builder().id(1).ownerId(1).feeType(FeeType.PROPERTY_FEE).build());
        mockMvc.perform(get("/api/fee/1")).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.ownerId").value(1));
    }

    @Test
    @DisplayName("GET /api/fee/{id} - 不存在返回404")
    void getById_notFound() throws Exception {
        when(feeService.getById(999)).thenReturn(null);
        mockMvc.perform(get("/api/fee/999")).andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(404));
    }

    @Test
    @DisplayName("POST /api/fee - 新增成功")
    void add_success() throws Exception {
        FeeCreateRequest req = FeeCreateRequest.builder().ownerId(1).feeType("PROPERTY_FEE").amount(BigDecimal.valueOf(1200)).shouldPayDate(LocalDate.of(2024, 6, 1)).build();
        when(feeService.add(any(Fee.class))).thenReturn(1);
        mockMvc.perform(post("/api/fee").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.msg").value("新增成功"));
    }

    @Test
    @DisplayName("PUT /api/fee/{id} - 修改成功（缴费）")
    void update_success() throws Exception {
        FeeUpdateRequest req = FeeUpdateRequest.builder().id(1).status("PAID").build();
        when(feeService.update(any(Fee.class))).thenReturn(1);
        mockMvc.perform(put("/api/fee/1").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.msg").value("修改成功"));
    }

    @Test
    @DisplayName("PUT /api/fee/{id} - 已缴不可退回返回400")
    void update_fails_irreversible() throws Exception {
        FeeUpdateRequest req = FeeUpdateRequest.builder().id(1).status("UNPAID").build();
        when(feeService.update(any(Fee.class))).thenThrow(new BusinessException("费用不允许从「已缴」转换为「未缴」"));
        mockMvc.perform(put("/api/fee/1").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("DELETE /api/fee/{id} - 删除成功")
    void delete_success() throws Exception {
        when(feeService.delete(1)).thenReturn(1);
        mockMvc.perform(delete("/api/fee/1")).andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("删除成功"));
    }
}
