package com.property.service.impl;

import com.property.entity.Repair;
import com.property.mapper.RepairMapper;
import com.property.service.RepairService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RepairServiceImpl implements RepairService {

    @Autowired
    private RepairMapper repairMapper;

    @Override
    public Repair getById(Integer id) {
        return repairMapper.selectById(id);
    }

    @Override
    public List<Repair> getByCondition(Integer ownerId, String status) {
        return repairMapper.selectByCondition(ownerId, status);
    }

    @Override
    public Map<String, Object> getByPage(Integer ownerId, String status, Integer pageNum, Integer pageSize) {
        if (pageNum == null || pageNum < 1) pageNum = 1;
        if (pageSize == null || pageSize < 1) pageSize = 10;
        int offset = (pageNum - 1) * pageSize;
        List<Repair> list = repairMapper.selectByPage(ownerId, status, offset, pageSize);
        int total = repairMapper.selectCount(ownerId, status);
        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        result.put("pageNum", pageNum);
        result.put("pageSize", pageSize);
        result.put("totalPages", (int) Math.ceil((double) total / pageSize));
        return result;
    }

    @Override
    public List<Repair> getByOwnerId(Integer ownerId) {
        return repairMapper.selectByOwnerId(ownerId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int add(Repair repair) {
        return repairMapper.insert(repair);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(Repair repair) {
        return repairMapper.update(repair);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delete(Integer id) {
        return repairMapper.deleteById(id);
    }
}
