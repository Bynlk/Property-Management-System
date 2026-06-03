package com.property.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.property.common.BusinessException;
import com.property.common.PageResult;
import com.property.dto.ComplaintCreateRequest;
import com.property.dto.ComplaintUpdateRequest;
import com.property.entity.Complaint;
import com.property.enums.ComplaintStatus;
import com.property.mapper.*;
import com.property.security.JwtAuthenticationFilter;
import com.property.security.JwtUtil;
import com.property.security.LoginAttemptService;
import com.property.service.ComplaintService;
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

@WebMvcTest(value = ComplaintController.class, excludeAutoConfiguration = {MybatisAutoConfiguration.class})
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("ComplaintController 集成测试")
class ComplaintControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean private ComplaintService complaintService;
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
    @DisplayName("GET /api/complaint - 分页查询")
    void getPage() throws Exception {
        when(complaintService.getByPage(any(), anyString(), anyInt(), anyInt()))
                .thenReturn(PageResult.of(List.of(), 0, 1, 10));
        mockMvc.perform(get("/api/complaint")).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.total").value(0));
    }

    @Test
    @DisplayName("GET /api/complaint/{id} - 查询存在")
    void getById_exists() throws Exception {
        when(complaintService.getById(1)).thenReturn(Complaint.builder().id(1).title("噪音").build());
        mockMvc.perform(get("/api/complaint/1")).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("噪音"));
    }

    @Test
    @DisplayName("GET /api/complaint/{id} - 不存在返回404")
    void getById_notFound() throws Exception {
        when(complaintService.getById(999)).thenReturn(null);
        mockMvc.perform(get("/api/complaint/999")).andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(404));
    }

    @Test
    @DisplayName("POST /api/complaint - 新增成功")
    void add_success() throws Exception {
        ComplaintCreateRequest req = ComplaintCreateRequest.builder().ownerId(1).title("电梯故障").content("电梯经常卡顿").build();
        when(complaintService.add(any(Complaint.class))).thenReturn(1);
        mockMvc.perform(post("/api/complaint").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.msg").value("新增成功"));
    }

    @Test
    @DisplayName("PUT /api/complaint/{id} - 修改成功（状态流转）")
    void update_success() throws Exception {
        ComplaintUpdateRequest req = ComplaintUpdateRequest.builder().id(1).status("PROCESSING").build();
        when(complaintService.update(any(Complaint.class))).thenReturn(1);
        mockMvc.perform(put("/api/complaint/1").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.msg").value("修改成功"));
    }

    @Test
    @DisplayName("PUT /api/complaint/{id} - 非法状态转换返回400")
    void update_fails_invalidTransition() throws Exception {
        ComplaintUpdateRequest req = ComplaintUpdateRequest.builder().id(1).status("RESOLVED").build();
        when(complaintService.update(any(Complaint.class))).thenThrow(new BusinessException("投诉不允许从「待处理」转换为「已处理」"));
        mockMvc.perform(put("/api/complaint/1").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("DELETE /api/complaint/{id} - 删除成功")
    void delete_success() throws Exception {
        when(complaintService.delete(1)).thenReturn(1);
        mockMvc.perform(delete("/api/complaint/1")).andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("删除成功"));
    }

    @Test
    @DisplayName("GET /api/complaint/owner/{ownerId} - 查询业主投诉")
    void getByOwnerId() throws Exception {
        when(complaintService.getByOwnerId(1)).thenReturn(List.of(Complaint.builder().id(1).ownerId(1).build()));
        mockMvc.perform(get("/api/complaint/owner/1")).andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)));
    }
}
