package com.property.service.impl;

import com.property.entity.Duty;
import com.property.mapper.DutyMapper;
import com.property.service.DutyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DutyServiceImpl implements DutyService {

    @Autowired
    private DutyMapper dutyMapper;

    @Override
    public Duty getById(Integer id) {
        return dutyMapper.selectById(id);
    }

    @Override
    public List<Duty> getByCondition(Integer employeeId, String shift) {
        return dutyMapper.selectByCondition(employeeId, shift);
    }

    @Override
    public Map<String, Object> getByPage(Integer employeeId, String shift, Integer pageNum, Integer pageSize) {
        if (pageNum == null || pageNum < 1) pageNum = 1;
        if (pageSize == null || pageSize < 1) pageSize = 10;
        int offset = (pageNum - 1) * pageSize;
        List<Duty> list = dutyMapper.selectByPage(employeeId, shift, offset, pageSize);
        int total = dutyMapper.selectCount(employeeId, shift);
        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        result.put("pageNum", pageNum);
        result.put("pageSize", pageSize);
        result.put("totalPages", (int) Math.ceil((double) total / pageSize));
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int add(Duty duty) {
        return dutyMapper.insert(duty);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(Duty duty) {
        return dutyMapper.update(duty);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delete(Integer id) {
        return dutyMapper.deleteById(id);
    }
}
