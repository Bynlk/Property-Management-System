package com.property.controller;

import com.property.common.BaseCrudController;
import com.property.common.PageResult;
import com.property.common.Result;
import com.property.dto.OwnerCreateRequest;
import com.property.dto.OwnerUpdateRequest;
import com.property.entity.Owner;
import com.property.service.OwnerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 业主Controller
 */
@Slf4j
@RequiredArgsConstructor
@Tag(name = "业主管理", description = "业主信息的增删改查")
@RestController
@RequestMapping("/api/owner")
public class OwnerController extends BaseCrudController<Owner, OwnerCreateRequest, OwnerUpdateRequest, OwnerService> {

    private final OwnerService ownerService;

    @Override
    protected OwnerService getService() { return ownerService; }

    @Override
    protected String getEntityName() { return "业主"; }

    @Override
    protected Owner toEntity(OwnerCreateRequest req) {
        return Owner.builder()
                .name(req.getName())
                .gender(req.getGender())
                .phone(req.getPhone())
                .idCard(req.getIdCard())
                .moveInDate(req.getMoveInDate())
                .build();
    }

    @Override
    protected Owner toEntity(OwnerUpdateRequest req, Integer id) {
        return Owner.builder()
                .id(id)
                .name(req.getName())
                .gender(req.getGender())
                .phone(req.getPhone())
                .idCard(req.getIdCard())
                .moveInDate(req.getMoveInDate())
                .build();
    }

    @Override
    protected PageResult<Owner> doPage(Object... params) {
        return ownerService.getByPage("", "", (Integer) params[0], (Integer) params[1]);
    }

    @Override
    protected int doAdd(Owner entity) { return ownerService.add(entity); }

    @Override
    protected int doUpdate(Owner entity) { return ownerService.update(entity); }

    @Override
    protected int doDelete(Integer id) { return ownerService.delete(id); }

    @Override
    protected Owner doGetById(Integer id) { return ownerService.getById(id); }

    @Operation(summary = "分页查询业主", description = "支持按姓名、手机号模糊搜索")
    @GetMapping("/page")
    public Result<PageResult<Owner>> page(
            @RequestParam(defaultValue = "") String name,
            @RequestParam(defaultValue = "") String phone,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(ownerService.getByPage(name, phone, pageNum, pageSize));
    }
}
