package com.property.controller;

import com.property.common.BaseCrudController;
import com.property.common.PageResult;
import com.property.common.Result;
import com.property.dto.ParkingCreateRequest;
import com.property.dto.ParkingUpdateRequest;
import com.property.entity.Parking;
import com.property.enums.ParkingStatus;
import com.property.service.ParkingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 停车位Controller
 */
@Slf4j
@RequiredArgsConstructor
@Tag(name = "停车位管理", description = "停车位的增删改查")
@RestController
@RequestMapping("/api/parking")
public class ParkingController extends BaseCrudController<Parking, ParkingCreateRequest, ParkingUpdateRequest, ParkingService> {

    private final ParkingService parkingService;

    @Override
    protected ParkingService getService() { return parkingService; }

    @Override
    protected String getEntityName() { return "停车位"; }

    @Override
    protected Parking toEntity(ParkingCreateRequest req) {
        return Parking.builder()
                .spotNumber(req.getSpotNumber())
                .licensePlate(req.getLicensePlate())
                .ownerId(req.getOwnerId())
                .status(req.getStatus() != null ? ParkingStatus.valueOf(req.getStatus()) : null)
                .build();
    }

    @Override
    protected Parking toEntity(ParkingUpdateRequest req, Integer id) {
        return Parking.builder()
                .id(id)
                .spotNumber(req.getSpotNumber())
                .licensePlate(req.getLicensePlate())
                .ownerId(req.getOwnerId())
                .status(req.getStatus() != null ? ParkingStatus.valueOf(req.getStatus()) : null)
                .build();
    }

    @Override
    protected PageResult<Parking> doPage(Object... params) {
        return parkingService.getByPage("", "", (Integer) params[0], (Integer) params[1]);
    }

    @Override
    protected int doAdd(Parking entity) { return parkingService.add(entity); }

    @Override
    protected int doUpdate(Parking entity) { return parkingService.update(entity); }

    @Override
    protected int doDelete(Integer id) { return parkingService.delete(id); }

    @Override
    protected Parking doGetById(Integer id) { return parkingService.getById(id); }

    @Operation(summary = "分页查询停车位", description = "支持按车位编号模糊搜索、按使用状态筛选")
    @GetMapping("/page")
    public Result<PageResult<Parking>> page(
            @RequestParam(defaultValue = "") String spotNumber,
            @RequestParam(defaultValue = "") String status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(parkingService.getByPage(spotNumber, status, pageNum, pageSize));
    }

    @Operation(summary = "根据业主ID查询停车位", description = "查询指定业主绑定的所有停车位")
    @GetMapping("/owner/{ownerId}")
    public Result<List<Parking>> getByOwnerId(@PathVariable Integer ownerId) {
        return Result.success(parkingService.getByOwnerId(ownerId));
    }
}
