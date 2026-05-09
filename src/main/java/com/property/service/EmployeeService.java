package com.property.service;

import com.property.entity.Employee;
import java.util.List;
import java.util.Map;

/**
 * 员工Service接口
 */
public interface EmployeeService {
    Employee getById(Integer id);
    List<Employee> getByCondition(String name, String position);
    Map<String, Object> getByPage(String name, String position, Integer pageNum, Integer pageSize);
    int add(Employee employee);
    int update(Employee employee);
    int delete(Integer id);
}
