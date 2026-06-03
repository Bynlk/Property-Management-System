package com.property.service.impl;

import com.property.common.BaseService;
import com.property.common.PageHelper;
import com.property.common.PageResult;
import com.property.common.StatusValidator;
import com.property.entity.Duty;
import com.property.mapper.DutyMapper;
import com.property.service.DutyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 值班Service实现类
 */
@Service
@RequiredArgsConstructor
public class DutyServiceImpl extends BaseService<Duty, DutyMapper> implements DutyService {

    private final DutyMapper dutyMapper;

    @Override
    protected DutyMapper getMapper() {
        return dutyMapper;
    }

    @Override
    protected String getEntityName() {
        return "值班记录";
    }

    @Override
    public List<Duty> getByCondition(Integer employeeId, String shift) {
        return dutyMapper.selectByCondition(employeeId, shift);
    }

    @Override
    public PageResult<Duty> getByPage(Integer employeeId, String shift, Integer pageNum, Integer pageSize) {
        return PageHelper.doPage(pageNum, pageSize,
                params -> dutyMapper.selectByPage(employeeId, shift, params[0], params[1]),
                () -> dutyMapper.selectCount(employeeId, shift));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int add(Duty duty) {
        // 验证值班班次值合法性
        StatusValidator.validateDutyShift(duty.getShift());
        return dutyMapper.insert(duty);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(Duty duty) {
        findExistingOrThrow(duty.getId());
        // 验证值班班次值合法性
        StatusValidator.validateDutyShift(duty.getShift());
        return dutyMapper.update(duty);
    }
}
