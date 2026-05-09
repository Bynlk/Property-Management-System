package com.property.mapper;

import com.property.entity.Duty;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 值班Mapper接口
 */
public interface DutyMapper {
    Duty selectById(@Param("id") Integer id);
    List<Duty> selectByCondition(@Param("employeeId") Integer employeeId, @Param("shift") String shift);
    List<Duty> selectByPage(@Param("employeeId") Integer employeeId, @Param("shift") String shift,
                            @Param("offset") Integer offset, @Param("pageSize") Integer pageSize);
    int selectCount(@Param("employeeId") Integer employeeId, @Param("shift") String shift);
    int insert(Duty duty);
    int update(Duty duty);
    int deleteById(@Param("id") Integer id);
}
