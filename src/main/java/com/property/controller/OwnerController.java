package com.property.controller;

import com.property.entity.Owner;
import com.property.service.OwnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/owner")
public class OwnerController {

    @Autowired
    private OwnerService ownerService;

    @GetMapping("/page")
    public Map<String, Object> page(
            @RequestParam(defaultValue = "") String name,
            @RequestParam(defaultValue = "") String phone,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return ownerService.getByPage(name, phone, pageNum, pageSize);
    }

    @GetMapping("/get/{id}")
    public Owner getById(@PathVariable Integer id) {
        return ownerService.getById(id);
    }

    @PostMapping("/add")
    public Map<String, Object> add(@RequestBody Owner owner) {
        Map<String, Object> result = new HashMap<>();
        try {
            int rows = ownerService.add(owner);
            result.put("code", rows > 0 ? 0 : 1);
            result.put("msg", rows > 0 ? "新增成功" : "新增失败");
        } catch (Exception e) {
            result.put("code", 1);
            result.put("msg", "操作异常，请联系管理员");
        }
        return result;
    }

    @PostMapping("/update")
    public Map<String, Object> update(@RequestBody Owner owner) {
        Map<String, Object> result = new HashMap<>();
        try {
            int rows = ownerService.update(owner);
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
            int rows = ownerService.delete(id);
            result.put("code", rows > 0 ? 0 : 1);
            result.put("msg", rows > 0 ? "删除成功" : "删除失败");
        } catch (Exception e) {
            result.put("code", 1);
            result.put("msg", "操作异常，请联系管理员");
        }
        return result;
    }
}
