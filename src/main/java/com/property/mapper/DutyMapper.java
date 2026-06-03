package com.property.mapper;

import com.property.common.BaseMapper;
import com.property.entity.Duty;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 值班Mapper接口
 */
public interface DutyMapper extends BaseMapper<Duty> {

    /** 条件查询 */
    List<Duty> selectByCondition(@Param("employeeId") Integer employeeId, @Param("shift") String shift);

    /** 分页查询 */
    List<Duty> selectByPage(@Param("employeeId") Integer employeeId, @Param("shift") String shift,
                            @Param("offset") Integer offset, @Param("pageSize") Integer pageSize);

    /** 查询总记录数 */
    int selectCount(@Param("employeeId") Integer employeeId, @Param("shift") String shift);

    /** 按员工ID统计值班数量 */
    int countByEmployeeId(@Param("employeeId") Integer employeeId);
}
