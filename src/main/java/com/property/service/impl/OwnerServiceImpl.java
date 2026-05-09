package com.property.service.impl;

import com.property.entity.Owner;
import com.property.mapper.OwnerMapper;
import com.property.service.OwnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 业主Service实现类
 */
@Service
public class OwnerServiceImpl implements OwnerService {

    @Autowired
    private OwnerMapper ownerMapper;

    @Override
    public Owner getById(Integer id) {
        return ownerMapper.selectById(id);
    }

    @Override
    public List<Owner> getByCondition(String name, String phone) {
        return ownerMapper.selectByCondition(name, phone);
    }

    @Override
    public Map<String, Object> getByPage(String name, String phone, Integer pageNum, Integer pageSize) {
        if (pageNum == null || pageNum < 1) pageNum = 1;
        if (pageSize == null || pageSize < 1) pageSize = 10;
        int offset = (pageNum - 1) * pageSize;
        List<Owner> list = ownerMapper.selectByPage(name, phone, offset, pageSize);
        int total = ownerMapper.selectCount(name, phone);
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
    public int add(Owner owner) {
        return ownerMapper.insert(owner);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(Owner owner) {
        return ownerMapper.update(owner);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delete(Integer id) {
        return ownerMapper.deleteById(id);
    }
}
