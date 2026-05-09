package com.property.service;

import com.property.entity.House;
import java.util.List;
import java.util.Map;

/**
 * 房屋Service接口
 */
public interface HouseService {
    House getById(Integer id);
    List<House> getByCondition(String building, String status);
    Map<String, Object> getByPage(String building, String status, Integer pageNum, Integer pageSize);
    List<House> getByOwnerId(Integer ownerId);
    int add(House house);
    int update(House house);
    int delete(Integer id);
}
