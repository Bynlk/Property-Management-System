package com.property.controller;

import com.property.common.BaseCrudController;
import com.property.common.PageResult;
import com.property.common.Result;
import com.property.dto.FeeCreateRequest;
import com.property.dto.FeeUpdateRequest;
import com.property.entity.Fee;
import com.property.enums.FeeStatus;
import com.property.enums.FeeType;
import com.property.service.FeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 费用Controller
 */
@Slf4j
@RequiredArgsConstructor
@Tag(name = "费用管理", description = "费用账单的增删改查")
@RestController
@RequestMapping("/api/fee")
public class FeeController extends BaseCrudController<Fee, FeeCreateRequest, FeeUpdateRequest, FeeService> {

    private final FeeService feeService;

    @Override
    protected FeeService getService() { return feeService; }

    @Override
    protected String getEntityName() { return "费用"; }

    @Override
    protected Fee toEntity(FeeCreateRequest req) {
        return Fee.builder()
                .ownerId(req.getOwnerId())
                .houseId(req.getHouseId())
                .feeType(req.getFeeType() != null ? FeeType.valueOf(req.getFeeType()) : null)
                .amount(req.getAmount())
                .shouldPayDate(req.getShouldPayDate())
                .paidDate(req.getPaidDate())
                .status(req.getStatus() != null ? FeeStatus.valueOf(req.getStatus()) : null)
                .build();
    }

    @Override
    protected Fee toEntity(FeeUpdateRequest req, Integer id) {
        return Fee.builder()
                .id(id)
                .ownerId(req.getOwnerId())
                .houseId(req.getHouseId())
                .feeType(req.getFeeType() != null ? FeeType.valueOf(req.getFeeType()) : null)
                .amount(req.getAmount())
                .shouldPayDate(req.getShouldPayDate())
                .paidDate(req.getPaidDate())
                .status(req.getStatus() != null ? FeeStatus.valueOf(req.getStatus()) : null)
                .build();
    }

    @Override
    protected PageResult<Fee> doPage(Object... params) {
        return feeService.getByPage(null, "", "", (Integer) params[0], (Integer) params[1]);
    }

    @Override
    protected int doAdd(Fee entity) { return feeService.add(entity); }

    @Override
    protected int doUpdate(Fee entity) { return feeService.update(entity); }

    @Override
    protected int doDelete(Integer id) { return feeService.delete(id); }

    @Override
    protected Fee doGetById(Integer id) { return feeService.getById(id); }

    @Operation(summary = "分页查询费用", description = "支持按业主ID、费用类型、缴费状态筛选")
    @GetMapping("/page")
    public Result<PageResult<Fee>> page(
            @RequestParam(required = false) Integer ownerId,
            @RequestParam(defaultValue = "") String feeType,
            @RequestParam(defaultValue = "") String status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(feeService.getByPage(ownerId, feeType, status, pageNum, pageSize));
    }
}
