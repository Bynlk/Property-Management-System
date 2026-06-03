package com.property.service;

import com.property.common.PageResult;
import com.property.entity.Complaint;
import java.util.List;

/**
 * 投诉Service接口
 */
public interface ComplaintService {
    Complaint getById(Integer id);
    List<Complaint> getByCondition(Integer ownerId, String status);
    PageResult<Complaint> getByPage(Integer ownerId, String status, Integer pageNum, Integer pageSize);
    List<Complaint> getByOwnerId(Integer ownerId);
    int count();
    int add(Complaint complaint);
    int update(Complaint complaint);
    int delete(Integer id);
}
