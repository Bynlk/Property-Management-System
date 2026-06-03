package com.property.mapper;

import com.property.common.BaseMapper;
import com.property.entity.Fee;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 费用Mapper接口
 */
public interface FeeMapper extends BaseMapper<Fee> {

    /** 条件查询 */
    List<Fee> selectByCondition(@Param("ownerId") Integer ownerId, @Param("feeType") String feeType, @Param("status") String status);

    /** 分页查询 */
    List<Fee> selectByPage(@Param("ownerId") Integer ownerId, @Param("feeType") String feeType, @Param("status") String status,
                           @Param("offset") Integer offset, @Param("pageSize") Integer pageSize);

    /** 查询总记录数 */
    int selectCount(@Param("ownerId") Integer ownerId, @Param("feeType") String feeType, @Param("status") String status);

    /** 查询全部记录数（无条件） */
    int countAll();

    /** 按业主ID查询费用 */
    List<Fee> selectByOwnerId(@Param("ownerId") Integer ownerId);

    /** 按业主ID统计费用数量 */
    int countByOwnerId(@Param("ownerId") Integer ownerId);
}
