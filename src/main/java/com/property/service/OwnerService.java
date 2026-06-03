package com.property.service;

import com.property.common.PageResult;
import com.property.entity.Owner;
import java.util.List;

/**
 * 业主Service接口
 */
public interface OwnerService {
    Owner getById(Integer id);
    List<Owner> getByCondition(String name, String phone);
    PageResult<Owner> getByPage(String name, String phone, Integer pageNum, Integer pageSize);
    int count();
    int add(Owner owner);
    int update(Owner owner);
    int delete(Integer id);
}
