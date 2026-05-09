package com.property.mapper;

import com.property.entity.Employee;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 员工Mapper接口
 */
public interface EmployeeMapper {
    Employee selectById(@Param("id") Integer id);
    List<Employee> selectByCondition(@Param("name") String name, @Param("position") String position);
    List<Employee> selectByPage(@Param("name") String name, @Param("position") String position,
                                @Param("offset") Integer offset, @Param("pageSize") Integer pageSize);
    int selectCount(@Param("name") String name, @Param("position") String position);
    int insert(Employee employee);
    int update(Employee employee);
    int deleteById(@Param("id") Integer id);
}
