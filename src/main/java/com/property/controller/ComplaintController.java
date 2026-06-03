package com.property.controller;

import com.property.common.BaseCrudController;
import com.property.common.PageResult;
import com.property.common.Result;
import com.property.dto.ComplaintCreateRequest;
import com.property.dto.ComplaintUpdateRequest;
import com.property.entity.Complaint;
import com.property.enums.ComplaintStatus;
import com.property.service.ComplaintService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 投诉Controller
 */
@Slf4j
@RequiredArgsConstructor
@Tag(name = "投诉管理", description = "投诉的增删改查及状态流转")
@RestController
@RequestMapping("/api/complaint")
public class ComplaintController extends BaseCrudController<Complaint, ComplaintCreateRequest, ComplaintUpdateRequest, ComplaintService> {

    private final ComplaintService complaintService;

    @Override
    protected ComplaintService getService() { return complaintService; }

    @Override
    protected String getEntityName() { return "投诉"; }

    @Override
    protected Complaint toEntity(ComplaintCreateRequest req) {
        return Complaint.builder()
                .ownerId(req.getOwnerId())
                .title(req.getTitle())
                .content(req.getContent())
                .status(req.getStatus() != null ? ComplaintStatus.valueOf(req.getStatus()) : null)
                .build();
    }

    @Override
    protected Complaint toEntity(ComplaintUpdateRequest req, Integer id) {
        return Complaint.builder()
                .id(id)
                .ownerId(req.getOwnerId())
                .title(req.getTitle())
                .content(req.getContent())
                .status(req.getStatus() != null ? ComplaintStatus.valueOf(req.getStatus()) : null)
                .build();
    }

    @Override
    protected PageResult<Complaint> doPage(Object... params) {
        return complaintService.getByPage(null, "", (Integer) params[0], (Integer) params[1]);
    }

    @Override
    protected int doAdd(Complaint entity) { return complaintService.add(entity); }

    @Override
    protected int doUpdate(Complaint entity) { return complaintService.update(entity); }

    @Override
    protected int doDelete(Integer id) { return complaintService.delete(id); }

    @Override
    protected Complaint doGetById(Integer id) { return complaintService.getById(id); }

    @Operation(summary = "分页查询投诉", description = "支持按业主ID、处理状态筛选")
    @GetMapping("/page")
    public Result<PageResult<Complaint>> page(
            @RequestParam(required = false) Integer ownerId,
            @RequestParam(defaultValue = "") String status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(complaintService.getByPage(ownerId, status, pageNum, pageSize));
    }

    @Operation(summary = "根据业主ID查询投诉", description = "查询指定业主提交的所有投诉")
    @GetMapping("/owner/{ownerId}")
    public Result<List<Complaint>> getByOwnerId(@PathVariable Integer ownerId) {
        return Result.success(complaintService.getByOwnerId(ownerId));
    }
}
