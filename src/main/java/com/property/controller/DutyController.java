package com.property.controller;

import com.property.entity.Duty;
import com.property.service.DutyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/duty")
public class DutyController {

    @Autowired
    private DutyService dutyService;

    @GetMapping("/page")
    public Map<String, Object> page(
            @RequestParam(required = false) Integer employeeId,
            @RequestParam(defaultValue = "") String shift,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return dutyService.getByPage(employeeId, shift, pageNum, pageSize);
    }

    @GetMapping("/get/{id}")
    public Duty getById(@PathVariable Integer id) {
        return dutyService.getById(id);
    }

    @PostMapping("/add")
    public Map<String, Object> add(@RequestBody Duty duty) {
        Map<String, Object> result = new HashMap<>();
        try {
            int rows = dutyService.add(duty);
            result.put("code", rows > 0 ? 0 : 1);
            result.put("msg", rows > 0 ? "新增成功" : "新增失败");
        } catch (Exception e) {
            result.put("code", 1);
            result.put("msg", "操作异常，请联系管理员");
        }
        return result;
    }

    @PostMapping("/update")
    public Map<String, Object> update(@RequestBody Duty duty) {
        Map<String, Object> result = new HashMap<>();
        try {
            int rows = dutyService.update(duty);
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
            int rows = dutyService.delete(id);
            result.put("code", rows > 0 ? 0 : 1);
            result.put("msg", rows > 0 ? "删除成功" : "删除失败");
        } catch (Exception e) {
            result.put("code", 1);
            result.put("msg", "操作异常，请联系管理员");
        }
        return result;
    }
}
