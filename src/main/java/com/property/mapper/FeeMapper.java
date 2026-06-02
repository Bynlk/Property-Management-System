package com.property.mapper;

import com.property.entity.Fee;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 欠费Mapper接口
 */
public interface FeeMapper {
    Fee selectById(@Param("id") Integer id);
    List<Fee> selectByCondition(@Param("ownerId") Integer ownerId, @Param("feeType") String feeType, @Param("status") String status);
    List<Fee> selectByPage(@Param("ownerId") Integer ownerId, @Param("feeType") String feeType, @Param("status") String status,
                           @Param("offset") Integer offset, @Param("pageSize") Integer pageSize);
    int selectCount(@Param("ownerId") Integer ownerId, @Param("feeType") String feeType, @Param("status") String status);
    List<Fee> selectByOwnerId(@Param("ownerId") Integer ownerId);
    int countByOwnerId(@Param("ownerId") Integer ownerId);
    int insert(Fee fee);
    int update(Fee fee);
    int deleteById(@Param("id") Integer id);
}
