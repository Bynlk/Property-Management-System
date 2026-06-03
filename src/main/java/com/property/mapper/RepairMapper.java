package com.property.mapper;

import com.property.common.BaseMapper;
import com.property.entity.Repair;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 报修Mapper接口
 */
public interface RepairMapper extends BaseMapper<Repair> {

    /** 条件查询 */
    List<Repair> selectByCondition(@Param("ownerId") Integer ownerId, @Param("status") String status);

    /** 分页查询 */
    List<Repair> selectByPage(@Param("ownerId") Integer ownerId, @Param("status") String status,
                              @Param("offset") Integer offset, @Param("pageSize") Integer pageSize);

    /** 查询总记录数 */
    int selectCount(@Param("ownerId") Integer ownerId, @Param("status") String status);

    /** 查询全部记录数（无条件） */
    int countAll();

    /** 按业主ID查询报修 */
    List<Repair> selectByOwnerId(@Param("ownerId") Integer ownerId);

    /** 按业主ID统计报修数量 */
    int countByOwnerId(@Param("ownerId") Integer ownerId);
}
