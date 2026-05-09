package com.property.controller;

import com.property.entity.Parking;
import com.property.service.ParkingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/parking")
public class ParkingController {

    @Autowired
    private ParkingService parkingService;

    @GetMapping("/page")
    public Map<String, Object> page(
            @RequestParam(defaultValue = "") String spotNumber,
            @RequestParam(defaultValue = "") String status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return parkingService.getByPage(spotNumber, status, pageNum, pageSize);
    }

    @GetMapping("/get/{id}")
    public Parking getById(@PathVariable Integer id) {
        return parkingService.getById(id);
    }

    @GetMapping("/owner/{ownerId}")
    public List<Parking> getByOwnerId(@PathVariable Integer ownerId) {
        return parkingService.getByOwnerId(ownerId);
    }

    @PostMapping("/add")
    public Map<String, Object> add(@RequestBody Parking parking) {
        Map<String, Object> result = new HashMap<>();
        try {
            int rows = parkingService.add(parking);
            result.put("code", rows > 0 ? 0 : 1);
            result.put("msg", rows > 0 ? "新增成功" : "新增失败");
        } catch (Exception e) {
            result.put("code", 1);
            result.put("msg", "操作异常，请联系管理员");
        }
        return result;
    }

    @PostMapping("/update")
    public Map<String, Object> update(@RequestBody Parking parking) {
        Map<String, Object> result = new HashMap<>();
        try {
            int rows = parkingService.update(parking);
            result.put("code", rows > 0 ? 0 : 1);
            result.put("msg", rows > 0 ? "修改成功" : "修改失败");
        } catch (Exception e) {
            result.put("code", 1);
            result.put("msg", "操作异常，请联系管理员");
        }
        return result;
    }

    @PostMapping("/delete/{id}")
    public Map<String, Object> delete(@PathVariable Integer id) {
        Map<String, Object> result = new HashMap<>();
        try {
            int rows = parkingService.delete(id);
            result.put("code", rows > 0 ? 0 : 1);
            result.put("msg", rows > 0 ? "删除成功" : "删除失败");
        } catch (Exception e) {
            result.put("code", 1);
            result.put("msg", "操作异常，请联系管理员");
        }
        return result;
    }
}
