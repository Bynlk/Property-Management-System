package com.property.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.property.common.BusinessException;
import com.property.common.PageResult;
import com.property.dto.OwnerCreateRequest;
import com.property.dto.OwnerUpdateRequest;
import com.property.entity.Owner;
import com.property.mapper.*;
import com.property.security.JwtAuthenticationFilter;
import com.property.security.JwtUtil;
import com.property.security.LoginAttemptService;
import com.property.service.OwnerService;
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

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = OwnerController.class, excludeAutoConfiguration = {MybatisAutoConfiguration.class})
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("OwnerController 集成测试")
class OwnerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OwnerService ownerService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private LoginAttemptService loginAttemptService;

    @MockBean
    private PasswordEncoder passwordEncoder;

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
    @DisplayName("GET /api/owner - 分页查询返回空列表")
    void getPage_returnsEmptyList() throws Exception {
        PageResult<Owner> pageResult = PageResult.of(List.of(), 0, 1, 10);
        when(ownerService.getByPage(anyString(), anyString(), anyInt(), anyInt())).thenReturn(pageResult);

        mockMvc.perform(get("/api/owner")
                        .param("pageNum", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.total").value(0));
    }

    @Test
    @DisplayName("GET /api/owner/page - 带筛选条件查询")
    void getPageWithFilter_returnsResults() throws Exception {
        Owner owner = Owner.builder().id(1).name("刘备").phone("13900001111").build();
        PageResult<Owner> pageResult = PageResult.of(List.of(owner), 1, 1, 10);
        when(ownerService.getByPage(eq("刘备"), eq(""), anyInt(), anyInt())).thenReturn(pageResult);

        mockMvc.perform(get("/api/owner/page")
                        .param("name", "刘备")
                        .param("pageNum", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.list", hasSize(1)))
                .andExpect(jsonPath("$.data.list[0].name").value("刘备"));
    }

    @Test
    @DisplayName("GET /api/owner/{id} - 查询存在的业主")
    void getById_returnsOwner() throws Exception {
        Owner owner = Owner.builder().id(1).name("刘备").phone("13900001111").build();
        when(ownerService.getById(1)).thenReturn(owner);

        mockMvc.perform(get("/api/owner/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.name").value("刘备"));
    }

    @Test
    @DisplayName("GET /api/owner/{id} - 查询不存在的业主返回404")
    void getById_returns404_whenNotExists() throws Exception {
        when(ownerService.getById(999)).thenReturn(null);

        mockMvc.perform(get("/api/owner/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(404));
    }

    @Test
    @DisplayName("POST /api/owner - 新增业主成功")
    void addOwner_success() throws Exception {
        OwnerCreateRequest request = OwnerCreateRequest.builder()
                .name("赵云")
                .gender("男")
                .phone("13900004444")
                .idCard("110101199004041234")
                .moveInDate(LocalDate.of(2023, 1, 5))
                .build();
        when(ownerService.add(any(Owner.class))).thenReturn(1);

        mockMvc.perform(post("/api/owner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.msg").value("新增成功"));
    }

    @Test
    @DisplayName("PUT /api/owner/{id} - 修改业主成功")
    void updateOwner_success() throws Exception {
        OwnerUpdateRequest request = OwnerUpdateRequest.builder()
                .id(1)
                .name("刘备改")
                .gender("")
                .phone("")
                .idCard("")
                .build();
        when(ownerService.update(any(Owner.class))).thenReturn(1);

        mockMvc.perform(put("/api/owner/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.msg").value("修改成功"));
    }

    @Test
    @DisplayName("PUT /api/owner/{id} - 修改不存在的业主返回400")
    void updateOwner_fails_whenNotExists() throws Exception {
        OwnerUpdateRequest request = OwnerUpdateRequest.builder()
                .id(999)
                .name("不存在")
                .gender("")
                .phone("")
                .idCard("")
                .build();
        when(ownerService.update(any(Owner.class))).thenThrow(new BusinessException("业主不存在: id=999"));

        mockMvc.perform(put("/api/owner/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(1));
    }

    @Test
    @DisplayName("DELETE /api/owner/{id} - 删除业主成功")
    void deleteOwner_success() throws Exception {
        when(ownerService.delete(1)).thenReturn(1);

        mockMvc.perform(delete("/api/owner/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.msg").value("删除成功"));
    }

    @Test
    @DisplayName("DELETE /api/owner/{id} - 删除有关联数据的业主返回400")
    void deleteOwner_fails_whenHasAssociatedData() throws Exception {
        when(ownerService.delete(1)).thenThrow(new BusinessException("该业主下有 2 套房屋，请先解除关联后再删除"));

        mockMvc.perform(delete("/api/owner/1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.msg", containsString("房屋")));
    }
}
