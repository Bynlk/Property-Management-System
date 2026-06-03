package com.property.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.property.common.BusinessException;
import com.property.common.PageResult;
import com.property.dto.DutyCreateRequest;
import com.property.dto.DutyUpdateRequest;
import com.property.entity.Duty;
import com.property.enums.DutyShift;
import com.property.mapper.*;
import com.property.security.JwtAuthenticationFilter;
import com.property.security.JwtUtil;
import com.property.security.LoginAttemptService;
import com.property.service.DutyService;
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

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = DutyController.class, excludeAutoConfiguration = {MybatisAutoConfiguration.class})
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("DutyController 集成测试")
class DutyControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean private DutyService dutyService;
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
    @DisplayName("GET /api/duty - 分页查询")
    void getPage() throws Exception {
        when(dutyService.getByPage(any(), anyString(), anyInt(), anyInt()))
                .thenReturn(PageResult.of(List.of(), 0, 1, 10));
        mockMvc.perform(get("/api/duty")).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.total").value(0));
    }

    @Test
    @DisplayName("GET /api/duty/{id} - 查询存在")
    void getById_exists() throws Exception {
        when(dutyService.getById(1)).thenReturn(Duty.builder().id(1).employeeId(1).shift(DutyShift.MORNING).build());
        mockMvc.perform(get("/api/duty/1")).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.employeeId").value(1));
    }

    @Test
    @DisplayName("GET /api/duty/{id} - 不存在返回404")
    void getById_notFound() throws Exception {
        when(dutyService.getById(999)).thenReturn(null);
        mockMvc.perform(get("/api/duty/999")).andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(404));
    }

    @Test
    @DisplayName("POST /api/duty - 新增成功")
    void add_success() throws Exception {
        DutyCreateRequest req = DutyCreateRequest.builder().employeeId(1).dutyDate(LocalDate.now()).shift("MORNING").build();
        when(dutyService.add(any(Duty.class))).thenReturn(1);
        mockMvc.perform(post("/api/duty").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.msg").value("新增成功"));
    }

    @Test
    @DisplayName("PUT /api/duty/{id} - 修改成功")
    void update_success() throws Exception {
        DutyUpdateRequest req = DutyUpdateRequest.builder().id(1).shift("AFTERNOON").build();
        when(dutyService.update(any(Duty.class))).thenReturn(1);
        mockMvc.perform(put("/api/duty/1").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.msg").value("修改成功"));
    }

    @Test
    @DisplayName("PUT /api/duty/{id} - 不存在返回400")
    void update_fails_notExists() throws Exception {
        DutyUpdateRequest req = DutyUpdateRequest.builder().id(999).shift("MORNING").build();
        when(dutyService.update(any(Duty.class))).thenThrow(new BusinessException("值班记录不存在: id=999"));
        mockMvc.perform(put("/api/duty/999").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("DELETE /api/duty/{id} - 删除成功")
    void delete_success() throws Exception {
        when(dutyService.delete(1)).thenReturn(1);
        mockMvc.perform(delete("/api/duty/1")).andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("删除成功"));
    }
}
