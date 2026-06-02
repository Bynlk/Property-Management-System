package com.property.service.impl;

import com.property.common.BusinessException;
import com.property.common.PageHelper;
import com.property.common.PageResult;
import com.property.entity.Employee;
import com.property.mapper.DutyMapper;
import com.property.mapper.EmployeeMapper;
import com.property.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 员工Service实现类
 */
@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    @Autowired
    private DutyMapper dutyMapper;

    @Override
    public Employee getById(Integer id) {
        return employeeMapper.selectById(id);
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
    public int add(Employee employee) {
        return employeeMapper.insert(employee);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(Employee employee) {
        Employee existing = employeeMapper.selectById(employee.getId());
        if (existing == null) {
            throw new BusinessException("员工不存在: id=" + employee.getId());
        }
        return employeeMapper.update(employee);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delete(Integer id) {
        Employee existing = employeeMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException("员工不存在: id=" + id);
        }
        // 检查是否有关联值班记录
        int dutyCount = dutyMapper.countByEmployeeId(id);
        if (dutyCount > 0) {
            throw new BusinessException("该员工有 " + dutyCount + " 条值班记录，请先删除相关值班后再删除");
        }
        return employeeMapper.deleteById(id);
    }
}
