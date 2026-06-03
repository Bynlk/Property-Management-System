package com.property.service;

import com.property.common.PageResult;
import com.property.entity.Employee;
import java.util.List;

/**
 * 员工Service接口
 */
public interface EmployeeService {
    Employee getById(Integer id);
    List<Employee> getByCondition(String name, String position);
    PageResult<Employee> getByPage(String name, String position, Integer pageNum, Integer pageSize);
    int add(Employee employee);
    int update(Employee employee);
    int delete(Integer id);
}
