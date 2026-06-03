package com.property.mapper;

import com.property.common.BaseMapper;
import com.property.entity.Parking;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 停车位Mapper接口
 */
public interface ParkingMapper extends BaseMapper<Parking> {

    /** 模糊查询（按车位号和状态） */
    List<Parking> selectByCondition(@Param("spotNumber") String spotNumber, @Param("status") String status);

    /** 分页查询 */
    List<Parking> selectByPage(@Param("spotNumber") String spotNumber, @Param("status") String status,
                               @Param("offset") Integer offset, @Param("pageSize") Integer pageSize);

    /** 查询总记录数 */
    int selectCount(@Param("spotNumber") String spotNumber, @Param("status") String status);

    /** 按业主ID查询停车位 */
    List<Parking> selectByOwnerId(@Param("ownerId") Integer ownerId);
}
