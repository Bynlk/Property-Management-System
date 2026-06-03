package com.property.mapper;

import com.property.common.BaseMapper;
import com.property.entity.House;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 房屋Mapper接口
 */
public interface HouseMapper extends BaseMapper<House> {

    /** 模糊查询（按楼栋和状态） */
    List<House> selectByCondition(@Param("building") String building, @Param("status") String status);

    /** 分页查询 */
    List<House> selectByPage(@Param("building") String building, @Param("status") String status,
                             @Param("offset") Integer offset, @Param("pageSize") Integer pageSize);

    /** 查询总记录数 */
    int selectCount(@Param("building") String building, @Param("status") String status);

    /** 查询全部记录数（无条件） */
    int countAll();

    /** 按业主ID查询房屋 */
    List<House> selectByOwnerId(@Param("ownerId") Integer ownerId);

    /** 按业主ID统计房屋数量 */
    int countByOwnerId(@Param("ownerId") Integer ownerId);
}
