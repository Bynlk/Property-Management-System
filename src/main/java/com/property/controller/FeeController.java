package com.property.controller;

import com.property.entity.Fee;
import com.property.service.FeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/fee")
public class FeeController {

    @Autowired
    private FeeService feeService;

    @GetMapping("/page")
    public Map<String, Object> page(
            @RequestParam(required = false) Integer ownerId,
            @RequestParam(defaultValue = "") String feeType,
            @RequestParam(defaultValue = "") String status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return feeService.getByPage(ownerId, feeType, status, pageNum, pageSize);
    }

    @GetMapping("/get/{id}")
    public Fee getById(@PathVariable Integer id) {
        return feeService.getById(id);
    }

    @PostMapping("/add")
    public Map<String, Object> add(@RequestBody Fee fee) {
        Map<String, Object> result = new HashMap<>();
        try {
            int rows = feeService.add(fee);
            result.put("code", rows > 0 ? 0 : 1);
            result.put("msg", rows > 0 ? "新增成功" : "新增失败");
        } catch (Exception e) {
            result.put("code", 1);
            result.put("msg", "操作异常，请联系管理员");
        }
        return result;
    }

    @PostMapping("/update")
    public Map<String, Object> update(@RequestBody Fee fee) {
        Map<String, Object> result = new HashMap<>();
        try {
            int rows = feeService.update(fee);
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
            int rows = feeService.delete(id);
            result.put("code", rows > 0 ? 0 : 1);
            result.put("msg", rows > 0 ? "删除成功" : "删除失败");
        } catch (Exception e) {
            result.put("code", 1);
            result.put("msg", "操作异常，请联系管理员");
        }
        return result;
    }
}
