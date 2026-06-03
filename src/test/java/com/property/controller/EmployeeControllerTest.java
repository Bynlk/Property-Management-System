package com.property.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.property.common.BusinessException;
import com.property.common.PageResult;
import com.property.dto.EmployeeCreateRequest;
import com.property.dto.EmployeeUpdateRequest;
import com.property.entity.Employee;
import com.property.mapper.*;
import com.property.security.JwtAuthenticationFilter;
import com.property.security.JwtUtil;
import com.property.security.LoginAttemptService;
import com.property.service.EmployeeService;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = EmployeeController.class, excludeAutoConfiguration = {MybatisAutoConfiguration.class})
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("EmployeeController 集成测试")
class EmployeeControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean private EmployeeService employeeService;
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
    @DisplayName("GET /api/employee - 分页查询返回空列表")
    void getPage_returnsEmptyList() throws Exception {
        when(employeeService.getByPage(anyString(), anyString(), anyInt(), anyInt()))
                .thenReturn(PageResult.of(List.of(), 0, 1, 10));
        mockMvc.perform(get("/api/employee").param("pageNum", "1").param("pageSize", "10"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.total").value(0));
    }

    @Test
    @DisplayName("GET /api/employee/page - 带筛选条件查询")
    void getPageWithFilter() throws Exception {
        Employee emp = Employee.builder().id(1).name("张三").position("保安").build();
        when(employeeService.getByPage("张三", "", 1, 10)).thenReturn(PageResult.of(List.of(emp), 1, 1, 10));
        mockMvc.perform(get("/api/employee/page").param("name", "张三").param("pageNum", "1").param("pageSize", "10"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.list", hasSize(1)))
                .andExpect(jsonPath("$.data.list[0].name").value("张三"));
    }

    @Test
    @DisplayName("GET /api/employee/{id} - 查询存在的员工")
    void getById_returnsEmployee() throws Exception {
        when(employeeService.getById(1)).thenReturn(Employee.builder().id(1).name("张三").build());
        mockMvc.perform(get("/api/employee/1")).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("张三"));
    }

    @Test
    @DisplayName("GET /api/employee/{id} - 查询不存在的员工返回404")
    void getById_returns404() throws Exception {
        when(employeeService.getById(999)).thenReturn(null);
        mockMvc.perform(get("/api/employee/999")).andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(404));
    }

    @Test
    @DisplayName("POST /api/employee - 新增员工成功")
    void add_success() throws Exception {
        EmployeeCreateRequest req = EmployeeCreateRequest.builder().name("李四").gender("男").phone("13800001111").position("保洁").hireDate(LocalDate.of(2024, 1, 1)).build();
        when(employeeService.add(any(Employee.class))).thenReturn(1);
        mockMvc.perform(post("/api/employee").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.msg").value("新增成功"));
    }

    @Test
    @DisplayName("PUT /api/employee/{id} - 修改员工成功")
    void update_success() throws Exception {
        EmployeeUpdateRequest req = EmployeeUpdateRequest.builder().id(1).name("张三改").build();
        when(employeeService.update(any(Employee.class))).thenReturn(1);
        mockMvc.perform(put("/api/employee/1").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.msg").value("修改成功"));
    }

    @Test
    @DisplayName("PUT /api/employee/{id} - 修改不存在的员工返回400")
    void update_fails_whenNotExists() throws Exception {
        EmployeeUpdateRequest req = EmployeeUpdateRequest.builder().id(999).name("不存在").build();
        when(employeeService.update(any(Employee.class))).thenThrow(new BusinessException("员工不存在: id=999"));
        mockMvc.perform(put("/api/employee/999").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("DELETE /api/employee/{id} - 删除员工成功")
    void delete_success() throws Exception {
        when(employeeService.delete(1)).thenReturn(1);
        mockMvc.perform(delete("/api/employee/1")).andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("删除成功"));
    }

    @Test
    @DisplayName("DELETE /api/employee/{id} - 删除有关联值班的员工返回400")
    void delete_fails_whenHasDuties() throws Exception {
        when(employeeService.delete(1)).thenThrow(new BusinessException("该员工有 3 条值班记录，请先删除相关值班后再删除"));
        mockMvc.perform(delete("/api/employee/1")).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg", containsString("值班")));
    }
}
