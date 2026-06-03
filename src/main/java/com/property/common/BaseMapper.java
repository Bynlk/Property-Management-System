package com.property.common;

import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 通用 Mapper 基接口，定义所有 Mapper 公共的 CRUD 方法
 * @param <T> 实体类型
 */
public interface BaseMapper<T> {

    /** 根据ID查询 */
    T selectById(@Param("id") Integer id);

    /** 新增 */
    int insert(T entity);

    /** 修改 */
    int update(T entity);

    /** 根据ID删除 */
    int deleteById(@Param("id") Integer id);

    /** 查询全部 */
    List<T> selectAll();
}
