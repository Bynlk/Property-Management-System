package com.property.controller;

import com.property.common.Result;
import com.property.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 仪表盘Controller — 提供统计数据聚合接口
 */
@RequiredArgsConstructor
@Tag(name = "仪表盘", description = "统计数据聚合接口")
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final OwnerService ownerService;
    private final HouseService houseService;
    private final FeeService feeService;
    private final ComplaintService complaintService;
    private final RepairService repairService;

    /**
     * 获取各模块统计数据（一次请求替代前端 5 次分页查询）
     */
    @Operation(summary = "获取统计数据", description = "一次性返回业主、房屋、费用、投诉、报修各模块的总数统计")
    @GetMapping("/stats")
    public Result<Map<String, Integer>> stats() {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("owners", ownerService.count());
        stats.put("houses", houseService.count());
        stats.put("fees", feeService.count());
        stats.put("complaints", complaintService.count());
        stats.put("repairs", repairService.count());
        return Result.success(stats);
    }
}
