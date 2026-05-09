package com.property.service;

import com.property.entity.Owner;
import java.util.List;
import java.util.Map;

/**
 * 业主Service接口
 */
public interface OwnerService {
    Owner getById(Integer id);
    List<Owner> getByCondition(String name, String phone);
    Map<String, Object> getByPage(String name, String phone, Integer pageNum, Integer pageSize);
    int add(Owner owner);
    int update(Owner owner);
    int delete(Integer id);
}
