package com.property.mapper;

import com.property.common.BaseMapper;
import com.property.entity.Complaint;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 投诉Mapper接口
 */
public interface ComplaintMapper extends BaseMapper<Complaint> {

    /** 条件查询 */
    List<Complaint> selectByCondition(@Param("ownerId") Integer ownerId, @Param("status") String status);

    /** 分页查询 */
    List<Complaint> selectByPage(@Param("ownerId") Integer ownerId, @Param("status") String status,
                                 @Param("offset") Integer offset, @Param("pageSize") Integer pageSize);

    /** 查询总记录数 */
    int selectCount(@Param("ownerId") Integer ownerId, @Param("status") String status);

    /** 查询全部记录数（无条件） */
    int countAll();

    /** 按业主ID查询投诉 */
    List<Complaint> selectByOwnerId(@Param("ownerId") Integer ownerId);

    /** 按业主ID统计投诉数量 */
    int countByOwnerId(@Param("ownerId") Integer ownerId);
}
