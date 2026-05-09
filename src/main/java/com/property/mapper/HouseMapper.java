package com.property.mapper;

import com.property.entity.House;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 房屋Mapper接口
 */
public interface HouseMapper {
    House selectById(@Param("id") Integer id);
    List<House> selectByCondition(@Param("building") String building, @Param("status") String status);
    List<House> selectByPage(@Param("building") String building, @Param("status") String status,
                             @Param("offset") Integer offset, @Param("pageSize") Integer pageSize);
    int selectCount(@Param("building") String building, @Param("status") String status);
    List<House> selectByOwnerId(@Param("ownerId") Integer ownerId);
    int insert(House house);
    int update(House house);
    int deleteById(@Param("id") Integer id);
}
