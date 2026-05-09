package com.property.service.impl;

import com.property.entity.Employee;
import com.property.mapper.EmployeeMapper;
import com.property.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    @Override
    public Employee getById(Integer id) {
        return employeeMapper.selectById(id);
    }

    @Override
    public List<Employee> getByCondition(String name, String position) {
        return employeeMapper.selectByCondition(name, position);
    }

    @Override
    public Map<String, Object> getByPage(String name, String position, Integer pageNum, Integer pageSize) {
        if (pageNum == null || pageNum < 1) pageNum = 1;
        if (pageSize == null || pageSize < 1) pageSize = 10;
        int offset = (pageNum - 1) * pageSize;
        List<Employee> list = employeeMapper.selectByPage(name, position, offset, pageSize);
        int total = employeeMapper.selectCount(name, position);
        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        result.put("pageNum", pageNum);
        result.put("pageSize", pageSize);
        result.put("totalPages", (int) Math.ceil((double) total / pageSize));
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int add(Employee employee) {
        return employeeMapper.insert(employee);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(Employee employee) {
        return employeeMapper.update(employee);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delete(Integer id) {
        return employeeMapper.deleteById(id);
    }
}
