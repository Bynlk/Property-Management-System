package com.property.service;

import com.property.common.PageResult;
import com.property.entity.House;
import java.util.List;

/**
 * 房屋Service接口
 */
public interface HouseService {
    House getById(Integer id);
    List<House> getByCondition(String building, String status);
    PageResult<House> getByPage(String building, String status, Integer pageNum, Integer pageSize);
    List<House> getByOwnerId(Integer ownerId);
    int count();
    int add(House house);
    int update(House house);
    int delete(Integer id);
}
