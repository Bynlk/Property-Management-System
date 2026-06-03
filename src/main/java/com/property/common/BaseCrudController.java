package com.property.common;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * 通用 CRUD Controller 基类
 * 封装了分页查询、根据ID查询、新增、修改、删除的通用逻辑
 *
 * @param <T>  实体类型
 * @param <C>  创建请求DTO类型
 * @param <U>  更新请求DTO类型
 * @param <S>  Service类型
 */
@Slf4j
public abstract class BaseCrudController<T, C, U, S> {

    /** 获取 Service 实例 */
    protected abstract S getService();

    /** 获取资源名称（用于日志和错误消息） */
    protected abstract String getEntityName();

    /** 创建请求DTO -> 实体 */
    protected abstract T toEntity(C createRequest);

    /** 更新请求DTO -> 实体 */
    protected abstract T toEntity(U updateRequest, Integer id);

    /** Service 分页方法 */
    protected abstract PageResult<T> doPage(Object... params);

    /** Service 新增方法 */
    protected abstract int doAdd(T entity);

    /** Service 更新方法 */
    protected abstract int doUpdate(T entity);

    /** Service 删除方法 */
    protected abstract int doDelete(Integer id);

    /** Service 根据ID查询 */
    protected abstract T doGetById(Integer id);

    @Operation(summary = "分页查询", description = "支持分页参数：pageNum（页码）、pageSize（每页条数）")
    @GetMapping
    public Result<PageResult<T>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(doPage(pageNum, pageSize));
    }

    @Operation(summary = "根据ID查询", description = "根据主键ID查询单条记录，不存在则返回404")
    @GetMapping("/{id}")
    public Result<T> getById(@PathVariable Integer id) {
        T entity = doGetById(id);
        if (entity == null) return Result.error(404, getEntityName() + "不存在");
        return Result.success(entity);
    }

    @Operation(summary = "新增", description = "创建一条新的记录，需要管理员权限")
    @PostMapping
    public Result<Void> add(@Valid @RequestBody C request) {
        log.info("新增{}: {}", getEntityName(), request);
        T entity = toEntity(request);
        int rows = doAdd(entity);
        return rows > 0 ? Result.success("新增成功") : Result.error("新增失败");
    }

    @Operation(summary = "修改", description = "根据主键ID更新记录，需要管理员权限")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Integer id, @Valid @RequestBody U request) {
        log.info("修改{}: id={}", getEntityName(), id);
        T entity = toEntity(request, id);
        int rows = doUpdate(entity);
        return rows > 0 ? Result.success("修改成功") : Result.error("修改失败");
    }

    @Operation(summary = "删除", description = "根据主键ID删除记录，需要管理员权限")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        log.info("删除{}: id={}", getEntityName(), id);
        int rows = doDelete(id);
        return rows > 0 ? Result.success("删除成功") : Result.error("删除失败");
    }
}
