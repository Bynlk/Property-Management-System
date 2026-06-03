package com.property.service.impl;

import com.property.common.BaseService;
import com.property.common.BusinessException;
import com.property.common.PageHelper;
import com.property.common.PageResult;
import com.property.entity.Employee;
import com.property.mapper.DutyMapper;
import com.property.mapper.EmployeeMapper;
import com.property.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 员工Service实现类
 */
@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl extends BaseService<Employee, EmployeeMapper> implements EmployeeService {

    private final EmployeeMapper employeeMapper;
    private final DutyMapper dutyMapper;

    @Override
    protected EmployeeMapper getMapper() {
        return employeeMapper;
    }

    @Override
    protected String getEntityName() {
        return "员工";
    }

    @Override
    public List<Employee> getByCondition(String name, String position) {
        return employeeMapper.selectByCondition(name, position);
    }

    @Override
    public PageResult<Employee> getByPage(String name, String position, Integer pageNum, Integer pageSize) {
        return PageHelper.doPage(pageNum, pageSize,
                params -> employeeMapper.selectByPage(name, position, params[0], params[1]),
                () -> employeeMapper.selectCount(name, position));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delete(Integer id) {
        findExistingOrThrow(id);
        // 检查是否有关联值班记录
        int dutyCount = dutyMapper.countByEmployeeId(id);
        if (dutyCount > 0) {
            throw new BusinessException("该员工有 " + dutyCount + " 条值班记录，请先删除相关值班后再删除");
        }
        return employeeMapper.deleteById(id);
    }
}
