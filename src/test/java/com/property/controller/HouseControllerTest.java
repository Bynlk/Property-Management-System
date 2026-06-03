package com.property.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.property.common.BusinessException;
import com.property.common.PageResult;
import com.property.dto.HouseCreateRequest;
import com.property.dto.HouseUpdateRequest;
import com.property.entity.House;
import com.property.enums.HouseStatus;
import com.property.mapper.*;
import com.property.security.JwtAuthenticationFilter;
import com.property.security.JwtUtil;
import com.property.security.LoginAttemptService;
import com.property.service.HouseService;
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
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = HouseController.class, excludeAutoConfiguration = {MybatisAutoConfiguration.class})
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("HouseController 集成测试")
class HouseControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean private HouseService houseService;
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
    @DisplayName("GET /api/house - 分页查询")
    void getPage() throws Exception {
        when(houseService.getByPage(anyString(), anyString(), anyInt(), anyInt()))
                .thenReturn(PageResult.of(List.of(), 0, 1, 10));
        mockMvc.perform(get("/api/house")).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.total").value(0));
    }

    @Test
    @DisplayName("GET /api/house/page - 带筛选")
    void getPageWithFilter() throws Exception {
        House house = House.builder().id(1).building("A栋").roomNumber("101").build();
        when(houseService.getByPage("A栋", "", 1, 10)).thenReturn(PageResult.of(List.of(house), 1, 1, 10));
        mockMvc.perform(get("/api/house/page").param("building", "A栋"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.list", hasSize(1)));
    }

    @Test
    @DisplayName("GET /api/house/{id} - 查询存在")
    void getById_exists() throws Exception {
        when(houseService.getById(1)).thenReturn(House.builder().id(1).building("A栋").build());
        mockMvc.perform(get("/api/house/1")).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.building").value("A栋"));
    }

    @Test
    @DisplayName("GET /api/house/{id} - 不存在返回404")
    void getById_notFound() throws Exception {
        when(houseService.getById(999)).thenReturn(null);
        mockMvc.perform(get("/api/house/999")).andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(404));
    }

    @Test
    @DisplayName("POST /api/house - 新增成功")
    void add_success() throws Exception {
        HouseCreateRequest req = HouseCreateRequest.builder().building("B栋").roomNumber("201").area(BigDecimal.valueOf(89.5)).build();
        when(houseService.add(any(House.class))).thenReturn(1);
        mockMvc.perform(post("/api/house").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.msg").value("新增成功"));
    }

    @Test
    @DisplayName("PUT /api/house/{id} - 修改成功")
    void update_success() throws Exception {
        HouseUpdateRequest req = HouseUpdateRequest.builder().id(1).building("A栋").roomNumber("101").status("OCCUPIED").build();
        when(houseService.update(any(House.class))).thenReturn(1);
        mockMvc.perform(put("/api/house/1").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.msg").value("修改成功"));
    }

    @Test
    @DisplayName("PUT /api/house/{id} - 非法状态转换返回400")
    void update_fails_invalidTransition() throws Exception {
        HouseUpdateRequest req = HouseUpdateRequest.builder().id(1).status("RENOVATING").build();
        when(houseService.update(any(House.class))).thenThrow(new BusinessException("房屋不允许从「已入住」转换为「装修中」"));
        mockMvc.perform(put("/api/house/1").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("DELETE /api/house/{id} - 删除成功")
    void delete_success() throws Exception {
        when(houseService.delete(1)).thenReturn(1);
        mockMvc.perform(delete("/api/house/1")).andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("删除成功"));
    }

    @Test
    @DisplayName("GET /api/house/owner/{ownerId} - 查询业主房屋")
    void getByOwnerId() throws Exception {
        when(houseService.getByOwnerId(1)).thenReturn(List.of(House.builder().id(1).ownerId(1).build()));
        mockMvc.perform(get("/api/house/owner/1")).andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)));
    }
}
