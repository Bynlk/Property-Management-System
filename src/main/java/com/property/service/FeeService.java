package com.property.service;

import com.property.entity.Fee;
import java.util.List;
import java.util.Map;

/**
 * 欠费Service接口
 */
public interface FeeService {
    Fee getById(Integer id);
    List<Fee> getByCondition(Integer ownerId, String feeType, String status);
    Map<String, Object> getByPage(Integer ownerId, String feeType, String status, Integer pageNum, Integer pageSize);
    List<Fee> getByOwnerId(Integer ownerId);
    int add(Fee fee);
    int update(Fee fee);
    int delete(Integer id);
}
