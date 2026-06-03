package com.property.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.property.common.BusinessException;
import com.property.common.PageResult;
import com.property.dto.RepairCreateRequest;
import com.property.dto.RepairUpdateRequest;
import com.property.entity.Repair;
import com.property.enums.RepairStatus;
import com.property.mapper.*;
import com.property.security.JwtAuthenticationFilter;
import com.property.security.JwtUtil;
import com.property.security.LoginAttemptService;
import com.property.service.RepairService;
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

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = RepairController.class, excludeAutoConfiguration = {MybatisAutoConfiguration.class})
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("RepairController 集成测试")
class RepairControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
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
    @DisplayName("GET /api/repair - 分页查询")
    void getPage() throws Exception {
        when(repairService.getByPage(any(), anyString(), anyInt(), anyInt()))
                .thenReturn(PageResult.of(List.of(), 0, 1, 10));
        mockMvc.perform(get("/api/repair")).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.total").value(0));
    }

    @Test
    @DisplayName("GET /api/repair/{id} - 查询存在")
    void getById_exists() throws Exception {
        when(repairService.getById(1)).thenReturn(Repair.builder().id(1).deviceName("空调").build());
        mockMvc.perform(get("/api/repair/1")).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.deviceName").value("空调"));
    }

    @Test
    @DisplayName("GET /api/repair/{id} - 不存在返回404")
    void getById_notFound() throws Exception {
        when(repairService.getById(999)).thenReturn(null);
        mockMvc.perform(get("/api/repair/999")).andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(404));
    }

    @Test
    @DisplayName("POST /api/repair - 新增成功")
    void add_success() throws Exception {
        RepairCreateRequest req = RepairCreateRequest.builder().ownerId(1).deviceName("热水器").faultDescription("不出热水").build();
        when(repairService.add(any(Repair.class))).thenReturn(1);
        mockMvc.perform(post("/api/repair").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.msg").value("新增成功"));
    }

    @Test
    @DisplayName("PUT /api/repair/{id} - 修改成功（状态流转）")
    void update_success() throws Exception {
        RepairUpdateRequest req = RepairUpdateRequest.builder().id(1).status("IN_PROGRESS").build();
        when(repairService.update(any(Repair.class))).thenReturn(1);
        mockMvc.perform(put("/api/repair/1").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.msg").value("修改成功"));
    }

    @Test
    @DisplayName("PUT /api/repair/{id} - 终态不可逆返回400")
    void update_fails_irreversible() throws Exception {
        RepairUpdateRequest req = RepairUpdateRequest.builder().id(1).status("IN_PROGRESS").build();
        when(repairService.update(any(Repair.class))).thenThrow(new BusinessException("报修不允许从「已完成」转换为「维修中」"));
        mockMvc.perform(put("/api/repair/1").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("DELETE /api/repair/{id} - 删除成功")
    void delete_success() throws Exception {
        when(repairService.delete(1)).thenReturn(1);
        mockMvc.perform(delete("/api/repair/1")).andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("删除成功"));
    }

    @Test
    @DisplayName("GET /api/repair/owner/{ownerId} - 查询业主报修")
    void getByOwnerId() throws Exception {
        when(repairService.getByOwnerId(1)).thenReturn(List.of(Repair.builder().id(1).ownerId(1).build()));
        mockMvc.perform(get("/api/repair/owner/1")).andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)));
    }
}
