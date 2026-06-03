package com.property.service.impl;

import com.property.common.BaseService;
import com.property.common.PageHelper;
import com.property.common.PageResult;
import com.property.common.StatusValidator;
import com.property.entity.Repair;
import com.property.entity.StatusChangeLog;
import com.property.enums.RepairStatus;
import com.property.mapper.RepairMapper;
import com.property.mapper.StatusChangeLogMapper;
import com.property.service.RepairService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 报修Service实现类
 */
@Service
@RequiredArgsConstructor
public class RepairServiceImpl extends BaseService<Repair, RepairMapper> implements RepairService {

    private final RepairMapper repairMapper;
    private final StatusChangeLogMapper statusChangeLogMapper;

    @Override
    protected RepairMapper getMapper() {
        return repairMapper;
    }

    @Override
    protected String getEntityName() {
        return "报修";
    }

    @Override
    public int count() {
        return repairMapper.countAll();
    }

    @Override
    public List<Repair> getByCondition(Integer ownerId, String status) {
        return repairMapper.selectByCondition(ownerId, status);
    }

    @Override
    public PageResult<Repair> getByPage(Integer ownerId, String status, Integer pageNum, Integer pageSize) {
        return PageHelper.doPage(pageNum, pageSize,
                params -> repairMapper.selectByPage(ownerId, status, params[0], params[1]),
                () -> repairMapper.selectCount(ownerId, status));
    }

    @Override
    public List<Repair> getByOwnerId(Integer ownerId) {
        return repairMapper.selectByOwnerId(ownerId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(Repair repair) {
        Repair existing = findExistingOrThrow(repair.getId());
        // 验证状态转换合法性
        if (repair.getStatus() != null) {
            StatusValidator.validateRepairTransition(existing.getStatus(), repair.getStatus());
            // 状态变为"已完成"时自动设置完成时间
            if (repair.getStatus() == RepairStatus.COMPLETED && existing.getStatus() != RepairStatus.COMPLETED) {
                repair.setCompletedAt(LocalDateTime.now());
            }
            // 写入审计日志
            String currentUser = "unknown";
            var auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated()) {
                currentUser = (String) auth.getPrincipal();
            }
            statusChangeLogMapper.insert(StatusChangeLog.builder()
                    .entityType("repair")
                    .entityId(repair.getId())
                    .oldStatus(existing.getStatus().getValue())
                    .newStatus(repair.getStatus().getValue())
                    .changedBy(currentUser)
                    .changedAt(LocalDateTime.now())
                    .build());
        }
        return repairMapper.update(repair);
    }
}
