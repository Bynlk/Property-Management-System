package com.property.service;

import com.property.common.PageResult;
import com.property.entity.Fee;
import java.util.List;

/**
 * 欠费Service接口
 */
public interface FeeService {
    Fee getById(Integer id);
    List<Fee> getByCondition(Integer ownerId, String feeType, String status);
    PageResult<Fee> getByPage(Integer ownerId, String feeType, String status, Integer pageNum, Integer pageSize);
    List<Fee> getByOwnerId(Integer ownerId);
    int count();
    int add(Fee fee);
    int update(Fee fee);
    int delete(Integer id);
}
