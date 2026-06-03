package com.property.service.impl;

import com.property.common.BaseService;
import com.property.common.PageHelper;
import com.property.common.PageResult;
import com.property.common.StatusValidator;
import com.property.entity.Complaint;
import com.property.entity.StatusChangeLog;
import com.property.enums.ComplaintStatus;
import com.property.mapper.ComplaintMapper;
import com.property.mapper.StatusChangeLogMapper;
import com.property.service.ComplaintService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 投诉Service实现类
 */
@Service
@RequiredArgsConstructor
public class ComplaintServiceImpl extends BaseService<Complaint, ComplaintMapper> implements ComplaintService {

    private final ComplaintMapper complaintMapper;
    private final StatusChangeLogMapper statusChangeLogMapper;

    @Override
    protected ComplaintMapper getMapper() {
        return complaintMapper;
    }

    @Override
    protected String getEntityName() {
        return "投诉";
    }

    @Override
    public int count() {
        return complaintMapper.countAll();
    }

    @Override
    public List<Complaint> getByCondition(Integer ownerId, String status) {
        return complaintMapper.selectByCondition(ownerId, status);
    }

    @Override
    public PageResult<Complaint> getByPage(Integer ownerId, String status, Integer pageNum, Integer pageSize) {
        return PageHelper.doPage(pageNum, pageSize,
                params -> complaintMapper.selectByPage(ownerId, status, params[0], params[1]),
                () -> complaintMapper.selectCount(ownerId, status));
    }

    @Override
    public List<Complaint> getByOwnerId(Integer ownerId) {
        return complaintMapper.selectByOwnerId(ownerId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int add(Complaint complaint) {
        return complaintMapper.insert(complaint);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(Complaint complaint) {
        Complaint existing = findExistingOrThrow(complaint.getId());
        // 验证状态转换合法性
        if (complaint.getStatus() != null) {
            StatusValidator.validateComplaintTransition(existing.getStatus(), complaint.getStatus());
            // 状态变为"已处理"时自动设置解决时间
            if (complaint.getStatus() == ComplaintStatus.RESOLVED && existing.getStatus() != ComplaintStatus.RESOLVED) {
                complaint.setResolvedAt(LocalDateTime.now());
            }
            // 写入审计日志
            String currentUser = "unknown";
            var auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated()) {
                currentUser = (String) auth.getPrincipal();
            }
            statusChangeLogMapper.insert(StatusChangeLog.builder()
                    .entityType("complaint")
                    .entityId(complaint.getId())
                    .oldStatus(existing.getStatus().getValue())
                    .newStatus(complaint.getStatus().getValue())
                    .changedBy(currentUser)
                    .changedAt(LocalDateTime.now())
                    .build());
        }
        return complaintMapper.update(complaint);
    }
}
