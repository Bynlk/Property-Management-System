package com.property.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一响应封装类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {
    private int code;
    private String msg;
    private T data;

    /** 成功（无数据） */
    public static <T> Result<T> success() {
        return new Result<>(0, "操作成功", null);
    }

    /** 成功（带消息） */
    public static <T> Result<T> success(String msg) {
        return new Result<>(0, msg, null);
    }

    /** 成功（带数据） */
    public static <T> Result<T> success(T data) {
        return new Result<>(0, "操作成功", data);
    }

    /** 成功（带消息+数据） */
    public static <T> Result<T> success(String msg, T data) {
        return new Result<>(0, msg, data);
    }

    /** 失败 */
    public static <T> Result<T> error(String msg) {
        return new Result<>(1, msg, null);
    }

    /** 失败（自定义code） */
    public static <T> Result<T> error(int code, String msg) {
        return new Result<>(code, msg, null);
    }
}
