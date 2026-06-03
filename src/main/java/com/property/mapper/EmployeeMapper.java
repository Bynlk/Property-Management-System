package com.property.mapper;

import com.property.common.BaseMapper;
import com.property.entity.Employee;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 员工Mapper接口
 */
public interface EmployeeMapper extends BaseMapper<Employee> {

    /** 模糊查询（按姓名和职位） */
    List<Employee> selectByCondition(@Param("name") String name, @Param("position") String position);

    /** 分页查询 */
    List<Employee> selectByPage(@Param("name") String name, @Param("position") String position,
                                @Param("offset") Integer offset, @Param("pageSize") Integer pageSize);

    /** 查询总记录数 */
    int selectCount(@Param("name") String name, @Param("position") String position);
}
