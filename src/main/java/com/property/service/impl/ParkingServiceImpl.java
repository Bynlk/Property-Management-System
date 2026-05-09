package com.property.service.impl;

import com.property.entity.Parking;
import com.property.mapper.ParkingMapper;
import com.property.service.ParkingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ParkingServiceImpl implements ParkingService {

    @Autowired
    private ParkingMapper parkingMapper;

    @Override
    public Parking getById(Integer id) {
        return parkingMapper.selectById(id);
    }

    @Override
    public List<Parking> getByCondition(String spotNumber, String status) {
        return parkingMapper.selectByCondition(spotNumber, status);
    }

    @Override
    public Map<String, Object> getByPage(String spotNumber, String status, Integer pageNum, Integer pageSize) {
        if (pageNum == null || pageNum < 1) pageNum = 1;
        if (pageSize == null || pageSize < 1) pageSize = 10;
        int offset = (pageNum - 1) * pageSize;
        List<Parking> list = parkingMapper.selectByPage(spotNumber, status, offset, pageSize);
        int total = parkingMapper.selectCount(spotNumber, status);
        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        result.put("pageNum", pageNum);
        result.put("pageSize", pageSize);
        result.put("totalPages", (int) Math.ceil((double) total / pageSize));
        return result;
    }

    @Override
    public List<Parking> getByOwnerId(Integer ownerId) {
        return parkingMapper.selectByOwnerId(ownerId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int add(Parking parking) {
        return parkingMapper.insert(parking);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(Parking parking) {
        return parkingMapper.update(parking);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delete(Integer id) {
        return parkingMapper.deleteById(id);
    }
}
