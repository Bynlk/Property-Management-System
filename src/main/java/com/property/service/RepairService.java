package com.property.service;

import com.property.common.PageResult;
import com.property.entity.Repair;
import java.util.List;

/**
 * 报修Service接口
 */
public interface RepairService {
    Repair getById(Integer id);
    List<Repair> getByCondition(Integer ownerId, String status);
    PageResult<Repair> getByPage(Integer ownerId, String status, Integer pageNum, Integer pageSize);
    List<Repair> getByOwnerId(Integer ownerId);
    int count();
    int add(Repair repair);
    int update(Repair repair);
    int delete(Integer id);
}
