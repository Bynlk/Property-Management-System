package com.property.mapper;

import com.property.entity.Owner;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 业主Mapper接口
 */
public interface OwnerMapper {

    /** 根据ID查询业主 */
    Owner selectById(@Param("id") Integer id);

    /** 模糊查询（按姓名和手机号） */
    List<Owner> selectByCondition(@Param("name") String name, @Param("phone") String phone);

    /** 查询所有业主 */
    List<Owner> selectAll();

    /** 分页查询 */
    List<Owner> selectByPage(@Param("name") String name, @Param("phone") String phone,
                             @Param("offset") Integer offset, @Param("pageSize") Integer pageSize);

    /** 查询总记录数 */
    int selectCount(@Param("name") String name, @Param("phone") String phone);

    /** 新增业主 */
    int insert(Owner owner);

    /** 修改业主 */
    int update(Owner owner);

    /** 删除业主 */
    int deleteById(@Param("id") Integer id);
}
