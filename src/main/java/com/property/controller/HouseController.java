package com.property.controller;

import com.property.common.BaseCrudController;
import com.property.common.PageResult;
import com.property.common.Result;
import com.property.dto.HouseCreateRequest;
import com.property.dto.HouseUpdateRequest;
import com.property.entity.House;
import com.property.enums.HouseStatus;
import com.property.service.HouseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 房屋Controller
 */
@Slf4j
@RequiredArgsConstructor
@Tag(name = "房屋管理", description = "房屋信息的增删改查")
@RestController
@RequestMapping("/api/house")
public class HouseController extends BaseCrudController<House, HouseCreateRequest, HouseUpdateRequest, HouseService> {

    private final HouseService houseService;

    @Override
    protected HouseService getService() { return houseService; }

    @Override
    protected String getEntityName() { return "房屋"; }

    @Override
    protected House toEntity(HouseCreateRequest req) {
        return House.builder()
                .building(req.getBuilding())
                .unit(req.getUnit())
                .roomNumber(req.getRoomNumber())
                .area(req.getArea())
                .houseType(req.getHouseType())
                .ownerId(req.getOwnerId())
                .status(req.getStatus() != null ? HouseStatus.valueOf(req.getStatus()) : null)
                .build();
    }

    @Override
    protected House toEntity(HouseUpdateRequest req, Integer id) {
        return House.builder()
                .id(id)
                .building(req.getBuilding())
                .unit(req.getUnit())
                .roomNumber(req.getRoomNumber())
                .area(req.getArea())
                .houseType(req.getHouseType())
                .ownerId(req.getOwnerId())
                .status(req.getStatus() != null ? HouseStatus.valueOf(req.getStatus()) : null)
                .build();
    }

    @Override
    protected PageResult<House> doPage(Object... params) {
        return houseService.getByPage("", "", (Integer) params[0], (Integer) params[1]);
    }

    @Override
    protected int doAdd(House entity) { return houseService.add(entity); }

    @Override
    protected int doUpdate(House entity) { return houseService.update(entity); }

    @Override
    protected int doDelete(Integer id) { return houseService.delete(id); }

    @Override
    protected House doGetById(Integer id) { return houseService.getById(id); }

    @Operation(summary = "分页查询房屋", description = "支持按楼栋号模糊搜索、按入住状态筛选")
    @GetMapping("/page")
    public Result<PageResult<House>> page(
            @RequestParam(defaultValue = "") String building,
            @RequestParam(defaultValue = "") String status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(houseService.getByPage(building, status, pageNum, pageSize));
    }

    @Operation(summary = "根据业主ID查询房屋", description = "查询指定业主名下的所有房屋")
    @GetMapping("/owner/{ownerId}")
    public Result<List<House>> getByOwnerId(@PathVariable Integer ownerId) {
        return Result.success(houseService.getByOwnerId(ownerId));
    }
}
