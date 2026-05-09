package com.property.service;

import com.property.entity.Complaint;
import java.util.List;
import java.util.Map;

/**
 * 投诉Service接口
 */
public interface ComplaintService {
    Complaint getById(Integer id);
    List<Complaint> getByCondition(Integer ownerId, String status);
    Map<String, Object> getByPage(Integer ownerId, String status, Integer pageNum, Integer pageSize);
    List<Complaint> getByOwnerId(Integer ownerId);
    int add(Complaint complaint);
    int update(Complaint complaint);
    int delete(Integer id);
}
