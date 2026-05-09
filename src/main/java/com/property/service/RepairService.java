package com.property.service;

import com.property.entity.Repair;
import java.util.List;
import java.util.Map;

/**
 * 报修Service接口
 */
public interface RepairService {
    Repair getById(Integer id);
    List<Repair> getByCondition(Integer ownerId, String status);
    Map<String, Object> getByPage(Integer ownerId, String status, Integer pageNum, Integer pageSize);
    List<Repair> getByOwnerId(Integer ownerId);
    int add(Repair repair);
    int update(Repair repair);
    int delete(Integer id);
}
