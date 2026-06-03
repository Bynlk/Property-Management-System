package com.property.service;

import com.property.common.PageResult;
import com.property.entity.Parking;
import java.util.List;

/**
 * 停车位Service接口
 */
public interface ParkingService {
    Parking getById(Integer id);
    List<Parking> getByCondition(String spotNumber, String status);
    PageResult<Parking> getByPage(String spotNumber, String status, Integer pageNum, Integer pageSize);
    List<Parking> getByOwnerId(Integer ownerId);
    int add(Parking parking);
    int update(Parking parking);
    int delete(Integer id);
}
