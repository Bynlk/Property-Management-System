package com.property.service.impl;

import com.property.entity.Complaint;
import com.property.mapper.ComplaintMapper;
import com.property.service.ComplaintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ComplaintServiceImpl implements ComplaintService {

    @Autowired
    private ComplaintMapper complaintMapper;

    @Override
    public Complaint getById(Integer id) {
        return complaintMapper.selectById(id);
    }

    @Override
    public List<Complaint> getByCondition(Integer ownerId, String status) {
        return complaintMapper.selectByCondition(ownerId, status);
    }

    @Override
    public Map<String, Object> getByPage(Integer ownerId, String status, Integer pageNum, Integer pageSize) {
        if (pageNum == null || pageNum < 1) pageNum = 1;
        if (pageSize == null || pageSize < 1) pageSize = 10;
        int offset = (pageNum - 1) * pageSize;
        List<Complaint> list = complaintMapper.selectByPage(ownerId, status, offset, pageSize);
        int total = complaintMapper.selectCount(ownerId, status);
        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        result.put("pageNum", pageNum);
        result.put("pageSize", pageSize);
        result.put("totalPages", (int) Math.ceil((double) total / pageSize));
        return result;
    }

    @Override
    public List<Complaint> getByOwnerId(Integer ownerId) {
        return complaintMapper.selectByOwnerId(ownerId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int add(Complaint complaint) {
        if (complaint.getCreateTime() == null) {
            complaint.setCreateTime(new Date());
        }
        return complaintMapper.insert(complaint);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(Complaint complaint) {
        return complaintMapper.update(complaint);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delete(Integer id) {
        return complaintMapper.deleteById(id);
    }
}
