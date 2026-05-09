package com.property.mapper;

import com.property.entity.Repair;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 报修Mapper接口
 */
public interface RepairMapper {
    Repair selectById(@Param("id") Integer id);
    List<Repair> selectByCondition(@Param("ownerId") Integer ownerId, @Param("status") String status);
    List<Repair> selectByPage(@Param("ownerId") Integer ownerId, @Param("status") String status,
                              @Param("offset") Integer offset, @Param("pageSize") Integer pageSize);
    int selectCount(@Param("ownerId") Integer ownerId, @Param("status") String status);
    List<Repair> selectByOwnerId(@Param("ownerId") Integer ownerId);
    int insert(Repair repair);
    int update(Repair repair);
    int deleteById(@Param("id") Integer id);
}
