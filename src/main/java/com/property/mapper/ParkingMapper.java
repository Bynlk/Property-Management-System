package com.property.mapper;

import com.property.entity.Parking;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 停车位Mapper接口
 */
public interface ParkingMapper {
    Parking selectById(@Param("id") Integer id);
    List<Parking> selectByCondition(@Param("spotNumber") String spotNumber, @Param("status") String status);
    List<Parking> selectByPage(@Param("spotNumber") String spotNumber, @Param("status") String status,
                               @Param("offset") Integer offset, @Param("pageSize") Integer pageSize);
    int selectCount(@Param("spotNumber") String spotNumber, @Param("status") String status);
    List<Parking> selectByOwnerId(@Param("ownerId") Integer ownerId);
    int insert(Parking parking);
    int update(Parking parking);
    int deleteById(@Param("id") Integer id);
}
