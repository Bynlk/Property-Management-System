package com.property.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.property.common.PageResult;
import com.property.dto.ParkingCreateRequest;
import com.property.dto.ParkingUpdateRequest;
import com.property.entity.Parking;
import com.property.enums.ParkingStatus;
import com.property.mapper.*;
import com.property.security.JwtAuthenticationFilter;
import com.property.security.JwtUtil;
import com.property.security.LoginAttemptService;
import com.property.service.ParkingService;
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

@WebMvcTest(value = ParkingController.class, excludeAutoConfiguration = {MybatisAutoConfiguration.class})
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("ParkingController 集成测试")
class ParkingControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean private ParkingService parkingService;
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
    @DisplayName("GET /api/parking - 分页查询")
    void getPage() throws Exception {
        when(parkingService.getByPage(anyString(), anyString(), anyInt(), anyInt()))
                .thenReturn(PageResult.of(List.of(), 0, 1, 10));
        mockMvc.perform(get("/api/parking")).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.total").value(0));
    }

    @Test
    @DisplayName("GET /api/parking/{id} - 查询存在")
    void getById_exists() throws Exception {
        when(parkingService.getById(1)).thenReturn(Parking.builder().id(1).spotNumber("A-001").build());
        mockMvc.perform(get("/api/parking/1")).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.spotNumber").value("A-001"));
    }

    @Test
    @DisplayName("GET /api/parking/{id} - 不存在返回404")
    void getById_notFound() throws Exception {
        when(parkingService.getById(999)).thenReturn(null);
        mockMvc.perform(get("/api/parking/999")).andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(404));
    }

    @Test
    @DisplayName("POST /api/parking - 新增成功")
    void add_success() throws Exception {
        ParkingCreateRequest req = ParkingCreateRequest.builder().spotNumber("B-001").build();
        when(parkingService.add(any(Parking.class))).thenReturn(1);
        mockMvc.perform(post("/api/parking").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.msg").value("新增成功"));
    }

    @Test
    @DisplayName("PUT /api/parking/{id} - 修改成功")
    void update_success() throws Exception {
        ParkingUpdateRequest req = ParkingUpdateRequest.builder().id(1).status("IN_USE").build();
        when(parkingService.update(any(Parking.class))).thenReturn(1);
        mockMvc.perform(put("/api/parking/1").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.msg").value("修改成功"));
    }

    @Test
    @DisplayName("DELETE /api/parking/{id} - 删除成功")
    void delete_success() throws Exception {
        when(parkingService.delete(1)).thenReturn(1);
        mockMvc.perform(delete("/api/parking/1")).andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("删除成功"));
    }

    @Test
    @DisplayName("GET /api/parking/owner/{ownerId} - 查询业主车位")
    void getByOwnerId() throws Exception {
        when(parkingService.getByOwnerId(1)).thenReturn(List.of(Parking.builder().id(1).ownerId(1).build()));
        mockMvc.perform(get("/api/parking/owner/1")).andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)));
    }
}
