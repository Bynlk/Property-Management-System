package com.property.common;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 通用 Service 基类，提供 CRUD 公共逻辑
 * @param <T> 实体类型
 * @param <M> Mapper 类型
 */
public abstract class BaseService<T, M extends BaseMapper<T>> {

    /** 子类注入的 Mapper 实例 */
    protected abstract M getMapper();

    /** 子类提供实体名称（用于异常消息） */
    protected abstract String getEntityName();

    /**
     * 根据ID查询
     */
    public T getById(Integer id) {
        return getMapper().selectById(id);
    }

    /**
     * 新增
     */
    @Transactional(rollbackFor = Exception.class)
    public int add(T entity) {
        return getMapper().insert(entity);
    }

    /**
     * 更新（含存在性检查）
     */
    @Transactional(rollbackFor = Exception.class)
    public int update(T entity) {
        Integer id = extractId(entity);
        findExistingOrThrow(id);
        return getMapper().update(entity);
    }

    /**
     * 删除（含存在性检查）
     */
    @Transactional(rollbackFor = Exception.class)
    public int delete(Integer id) {
        findExistingOrThrow(id);
        return getMapper().deleteById(id);
    }

    /**
     * 查询全部
     */
    public List<T> getAll() {
        return getMapper().selectAll();
    }

    /**
     * 根据ID查询，不存在则抛出异常（子类可调用）
     */
    protected T findExistingOrThrow(Integer id) {
        T existing = getMapper().selectById(id);
        if (existing == null) {
            throw new BusinessException(getEntityName() + "不存在: id=" + id);
        }
        return existing;
    }

    /**
     * 从实体中提取ID（通过反射）
     */
    protected Integer extractId(T entity) {
        try {
            return (Integer) entity.getClass().getMethod("getId").invoke(entity);
        } catch (Exception e) {
            throw new BusinessException("无法获取实体ID");
        }
    }
}
