package com.property.service.impl;

import com.property.entity.House;
import com.property.mapper.HouseMapper;
import com.property.service.HouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class HouseServiceImpl implements HouseService {

    @Autowired
    private HouseMapper houseMapper;

    @Override
    public House getById(Integer id) {
        return houseMapper.selectById(id);
    }

    @Override
    public List<House> getByCondition(String building, String status) {
        return houseMapper.selectByCondition(building, status);
    }

    @Override
    public Map<String, Object> getByPage(String building, String status, Integer pageNum, Integer pageSize) {
        if (pageNum == null || pageNum < 1) pageNum = 1;
        if (pageSize == null || pageSize < 1) pageSize = 10;
        int offset = (pageNum - 1) * pageSize;
        List<House> list = houseMapper.selectByPage(building, status, offset, pageSize);
        int total = houseMapper.selectCount(building, status);
        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        result.put("pageNum", pageNum);
        result.put("pageSize", pageSize);
        result.put("totalPages", (int) Math.ceil((double) total / pageSize));
        return result;
    }

    @Override
    public List<House> getByOwnerId(Integer ownerId) {
        return houseMapper.selectByOwnerId(ownerId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int add(House house) {
        return houseMapper.insert(house);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(House house) {
        return houseMapper.update(house);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delete(Integer id) {
        return houseMapper.deleteById(id);
    }
}
