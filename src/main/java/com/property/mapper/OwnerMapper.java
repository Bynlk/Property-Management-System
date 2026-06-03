package com.property.mapper;

import com.property.common.BaseMapper;
import com.property.entity.Owner;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 业主Mapper接口
 */
public interface OwnerMapper extends BaseMapper<Owner> {

    /** 模糊查询（按姓名和手机号） */
    List<Owner> selectByCondition(@Param("name") String name, @Param("phone") String phone);

    /** 分页查询 */
    List<Owner> selectByPage(@Param("name") String name, @Param("phone") String phone,
                             @Param("offset") Integer offset, @Param("pageSize") Integer pageSize);

    /** 查询总记录数 */
    int selectCount(@Param("name") String name, @Param("phone") String phone);

    /** 查询全部记录数（无条件） */
    int countAll();
}
