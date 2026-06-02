package com.property.service.impl;

import com.property.common.BusinessException;
import com.property.common.PageHelper;
import com.property.common.PageResult;
import com.property.common.StatusValidator;
import com.property.entity.Fee;
import com.property.mapper.FeeMapper;
import com.property.service.FeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 费用Service实现类
 */
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
    public int add(Fee fee) {
        return feeMapper.insert(fee);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(Fee fee) {
        // 必须先查询现有记录，验证存在性和状态转换
        Fee existing = feeMapper.selectById(fee.getId());
        if (existing == null) {
            throw new BusinessException("费用记录不存在: id=" + fee.getId());
        }
        // 验证状态转换合法性
        if (fee.getStatus() != null) {
            StatusValidator.validateFeeTransition(existing.getStatus(), fee.getStatus());
            // 状态变为"已缴"时，自动设置缴费日期
            if ("已缴".equals(fee.getStatus()) && !"已缴".equals(existing.getStatus())) {
                fee.setPaidDate(new Date());
            }
        }
        return feeMapper.update(fee);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delete(Integer id) {
        Fee existing = feeMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException("费用记录不存在: id=" + id);
        }
        return feeMapper.deleteById(id);
    }
}
