package com.property.mapper;

import com.property.entity.Complaint;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 投诉Mapper接口
 */
public interface ComplaintMapper {
    Complaint selectById(@Param("id") Integer id);
    List<Complaint> selectByCondition(@Param("ownerId") Integer ownerId, @Param("status") String status);
    List<Complaint> selectByPage(@Param("ownerId") Integer ownerId, @Param("status") String status,
                                 @Param("offset") Integer offset, @Param("pageSize") Integer pageSize);
    int selectCount(@Param("ownerId") Integer ownerId, @Param("status") String status);
    List<Complaint> selectByOwnerId(@Param("ownerId") Integer ownerId);
    int insert(Complaint complaint);
    int update(Complaint complaint);
    int deleteById(@Param("id") Integer id);
}
