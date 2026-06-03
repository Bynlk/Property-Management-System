package com.property.service.impl;

import com.property.common.BaseService;
import com.property.common.PageHelper;
import com.property.common.PageResult;
import com.property.common.StatusValidator;
import com.property.entity.Fee;
import com.property.enums.FeeStatus;
import com.property.mapper.FeeMapper;
import com.property.service.FeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * 费用Service实现类
 */
@Service
@RequiredArgsConstructor
public class FeeServiceImpl extends BaseService<Fee, FeeMapper> implements FeeService {

    private final FeeMapper feeMapper;

    @Override
    protected FeeMapper getMapper() {
        return feeMapper;
    }

    @Override
    protected String getEntityName() {
        return "费用记录";
    }

    @Override
    public int count() {
        return feeMapper.countAll();
    }

    @Override
    public List<Fee> getByCondition(Integer ownerId, String feeType, String status) {
        return feeMapper.selectByCondition(ownerId, feeType, status);
    }

    @Override
    public PageResult<Fee> getByPage(Integer ownerId, String feeType, String status, Integer pageNum, Integer pageSize) {
        return PageHelper.doPage(pageNum, pageSize,
                params -> feeMapper.selectByPage(ownerId, feeType, status, params[0], params[1]),
                () -> feeMapper.selectCount(ownerId, feeType, status));
    }

    @Override
    public List<Fee> getByOwnerId(Integer ownerId) {
        return feeMapper.selectByOwnerId(ownerId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(Fee fee) {
        Fee existing = findExistingOrThrow(fee.getId());
        // 验证状态转换合法性
        if (fee.getStatus() != null) {
            StatusValidator.validateFeeTransition(existing.getStatus(), fee.getStatus());
            // 状态变为"已缴"时，自动设置缴费日期
            if (FeeStatus.PAID.equals(fee.getStatus()) && !FeeStatus.PAID.equals(existing.getStatus())) {
                fee.setPaidDate(LocalDate.now());
            }
        }
        return feeMapper.update(fee);
    }
}
