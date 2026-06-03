package com.property.mapper;

import com.property.entity.StatusChangeLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 状态变更审计日志 Mapper
 */
public interface StatusChangeLogMapper {

    int insert(StatusChangeLog log);

    List<StatusChangeLog> selectByEntity(@Param("entityType") String entityType, @Param("entityId") Integer entityId);
}
