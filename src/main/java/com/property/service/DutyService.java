package com.property.service;

import com.property.common.PageResult;
import com.property.entity.Duty;
import java.util.List;

/**
 * 值班Service接口
 */
public interface DutyService {
    Duty getById(Integer id);
    List<Duty> getByCondition(Integer employeeId, String shift);
    PageResult<Duty> getByPage(Integer employeeId, String shift, Integer pageNum, Integer pageSize);
    int add(Duty duty);
    int update(Duty duty);
    int delete(Integer id);
}
