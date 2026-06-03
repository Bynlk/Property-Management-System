package com.property.controller;

import com.property.common.BaseCrudController;
import com.property.common.PageResult;
import com.property.common.Result;
import com.property.dto.EmployeeCreateRequest;
import com.property.dto.EmployeeUpdateRequest;
import com.property.entity.Employee;
import com.property.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 员工Controller
 */
@Slf4j
@RequiredArgsConstructor
@Tag(name = "员工管理", description = "员工信息的增删改查")
@RestController
@RequestMapping("/api/employee")
public class EmployeeController extends BaseCrudController<Employee, EmployeeCreateRequest, EmployeeUpdateRequest, EmployeeService> {

    private final EmployeeService employeeService;

    @Override
    protected EmployeeService getService() { return employeeService; }

    @Override
    protected String getEntityName() { return "员工"; }

    @Override
    protected Employee toEntity(EmployeeCreateRequest req) {
        return Employee.builder()
                .name(req.getName())
                .gender(req.getGender())
                .phone(req.getPhone())
                .position(req.getPosition())
                .hireDate(req.getHireDate())
                .build();
    }

    @Override
    protected Employee toEntity(EmployeeUpdateRequest req, Integer id) {
        return Employee.builder()
                .id(id)
                .name(req.getName())
                .gender(req.getGender())
                .phone(req.getPhone())
                .position(req.getPosition())
                .hireDate(req.getHireDate())
                .build();
    }

    @Override
    protected PageResult<Employee> doPage(Object... params) {
        return employeeService.getByPage("", "", (Integer) params[0], (Integer) params[1]);
    }

    @Override
    protected int doAdd(Employee entity) { return employeeService.add(entity); }

    @Override
    protected int doUpdate(Employee entity) { return employeeService.update(entity); }

    @Override
    protected int doDelete(Integer id) { return employeeService.delete(id); }

    @Override
    protected Employee doGetById(Integer id) { return employeeService.getById(id); }

    @Operation(summary = "分页查询员工", description = "支持按姓名、岗位模糊搜索")
    @GetMapping("/page")
    public Result<PageResult<Employee>> page(
            @RequestParam(defaultValue = "") String name,
            @RequestParam(defaultValue = "") String position,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(employeeService.getByPage(name, position, pageNum, pageSize));
    }
}
