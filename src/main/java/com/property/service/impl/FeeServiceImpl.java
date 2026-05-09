package com.property.service.impl;

import com.property.entity.Fee;
import com.property.mapper.FeeMapper;
import com.property.service.FeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FeeServiceImpl implements FeeService {

    @Autowired
    private FeeMapper feeMapper;

    @Override
    public Fee getById(Integer id) {
        return feeMapper.selectById(id);
    }

    @Override
    public List<Fee> getByCondition(Integer ownerId, String feeType, String status) {
        return feeMapper.selectByCondition(ownerId, feeType, status);
    }

    @Override
    public Map<String, Object> getByPage(Integer ownerId, String feeType, String status, Integer pageNum, Integer pageSize) {
        if (pageNum == null || pageNum < 1) pageNum = 1;
        if (pageSize == null || pageSize < 1) pageSize = 10;
        int offset = (pageNum - 1) * pageSize;
        List<Fee> list = feeMapper.selectByPage(ownerId, feeType, status, offset, pageSize);
        int total = feeMapper.selectCount(ownerId, feeType, status);
        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        result.put("pageNum", pageNum);
        result.put("pageSize", pageSize);
        result.put("totalPages", (int) Math.ceil((double) total / pageSize));
        return result;
    }

    @Override
    public List<Fee> getByOwnerId(Integer ownerId) {
        return feeMapper.selectByOwnerId(ownerId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int add(Fee fee) {
        return feeMapper.insert(fee);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(Fee fee) {
        return feeMapper.update(fee);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delete(Integer id) {
        return feeMapper.deleteById(id);
    }
}
