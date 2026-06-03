package com.property.controller;

import com.property.common.BaseCrudController;
import com.property.common.PageResult;
import com.property.common.Result;
import com.property.dto.DutyCreateRequest;
import com.property.dto.DutyUpdateRequest;
import com.property.entity.Duty;
import com.property.enums.DutyShift;
import com.property.service.DutyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 值班Controller
 */
@Slf4j
@RequiredArgsConstructor
@Tag(name = "值班管理", description = "值班排班的增删改查")
@RestController
@RequestMapping("/api/duty")
public class DutyController extends BaseCrudController<Duty, DutyCreateRequest, DutyUpdateRequest, DutyService> {

    private final DutyService dutyService;

    @Override
    protected DutyService getService() { return dutyService; }

    @Override
    protected String getEntityName() { return "值班"; }

    @Override
    protected Duty toEntity(DutyCreateRequest req) {
        return Duty.builder()
                .employeeId(req.getEmployeeId())
                .dutyDate(req.getDutyDate())
                .shift(req.getShift() != null ? DutyShift.valueOf(req.getShift()) : null)
                .build();
    }

    @Override
    protected Duty toEntity(DutyUpdateRequest req, Integer id) {
        return Duty.builder()
                .id(id)
                .employeeId(req.getEmployeeId())
                .dutyDate(req.getDutyDate())
                .shift(req.getShift() != null ? DutyShift.valueOf(req.getShift()) : null)
                .build();
    }

    @Override
    protected PageResult<Duty> doPage(Object... params) {
        return dutyService.getByPage(null, "", (Integer) params[0], (Integer) params[1]);
    }

    @Override
    protected int doAdd(Duty entity) { return dutyService.add(entity); }

    @Override
    protected int doUpdate(Duty entity) { return dutyService.update(entity); }

    @Override
    protected int doDelete(Integer id) { return dutyService.delete(id); }

    @Override
    protected Duty doGetById(Integer id) { return dutyService.getById(id); }

    @Operation(summary = "分页查询值班", description = "支持按员工ID、班次筛选")
    @GetMapping("/page")
    public Result<PageResult<Duty>> page(
            @RequestParam(required = false) Integer employeeId,
            @RequestParam(defaultValue = "") String shift,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(dutyService.getByPage(employeeId, shift, pageNum, pageSize));
    }
}
