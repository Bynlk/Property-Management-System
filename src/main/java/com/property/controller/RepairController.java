package com.property.controller;

import com.property.common.BaseCrudController;
import com.property.common.PageResult;
import com.property.common.Result;
import com.property.dto.RepairCreateRequest;
import com.property.dto.RepairUpdateRequest;
import com.property.entity.Repair;
import com.property.enums.RepairStatus;
import com.property.service.RepairService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 报修Controller
 */
@Slf4j
@RequiredArgsConstructor
@Tag(name = "报修管理", description = "报修的增删改查及状态流转")
@RestController
@RequestMapping("/api/repair")
public class RepairController extends BaseCrudController<Repair, RepairCreateRequest, RepairUpdateRequest, RepairService> {

    private final RepairService repairService;

    @Override
    protected RepairService getService() { return repairService; }

    @Override
    protected String getEntityName() { return "报修"; }

    @Override
    protected Repair toEntity(RepairCreateRequest req) {
        return Repair.builder()
                .ownerId(req.getOwnerId())
                .deviceName(req.getDeviceName())
                .faultDescription(req.getFaultDescription())
                .repairEmployeeId(req.getRepairEmployeeId())
                .status(req.getStatus() != null ? RepairStatus.valueOf(req.getStatus()) : null)
                .build();
    }

    @Override
    protected Repair toEntity(RepairUpdateRequest req, Integer id) {
        return Repair.builder()
                .id(id)
                .ownerId(req.getOwnerId())
                .deviceName(req.getDeviceName())
                .faultDescription(req.getFaultDescription())
                .repairEmployeeId(req.getRepairEmployeeId())
                .status(req.getStatus() != null ? RepairStatus.valueOf(req.getStatus()) : null)
                .build();
    }

    @Override
    protected PageResult<Repair> doPage(Object... params) {
        return repairService.getByPage(null, "", (Integer) params[0], (Integer) params[1]);
    }

    @Override
    protected int doAdd(Repair entity) { return repairService.add(entity); }

    @Override
    protected int doUpdate(Repair entity) { return repairService.update(entity); }

    @Override
    protected int doDelete(Integer id) { return repairService.delete(id); }

    @Override
    protected Repair doGetById(Integer id) { return repairService.getById(id); }

    @Operation(summary = "分页查询报修", description = "支持按业主ID、维修状态筛选")
    @GetMapping("/page")
    public Result<PageResult<Repair>> page(
            @RequestParam(required = false) Integer ownerId,
            @RequestParam(defaultValue = "") String status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(repairService.getByPage(ownerId, status, pageNum, pageSize));
    }

    @Operation(summary = "根据业主ID查询报修", description = "查询指定业主提交的所有报修")
    @GetMapping("/owner/{ownerId}")
    public Result<List<Repair>> getByOwnerId(@PathVariable Integer ownerId) {
        return Result.success(repairService.getByOwnerId(ownerId));
    }
}
