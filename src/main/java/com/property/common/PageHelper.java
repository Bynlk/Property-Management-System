package com.property.common;

import java.util.List;
import java.util.function.BiFunction;

/**
 * 分页工具类 — 消除各 Service 中重复的分页逻辑
 */
public class PageHelper {

    private static final int MAX_PAGE_SIZE = 100;

    /**
     * 标准化分页参数
     * @return 归一化后的 [pageNum, pageSize]
     */
    public static int[] normalize(Integer pageNum, Integer pageSize) {
        int pn = (pageNum == null || pageNum < 1) ? 1 : pageNum;
        int ps = (pageSize == null || pageSize < 1) ? 10 : Math.min(pageSize, MAX_PAGE_SIZE);
        return new int[]{pn, ps};
    }

    /**
     * 执行分页查询并构建 PageResult
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @param listQuerier 查询列表的函数 (offset, limit) -> List<T>
     * @param countQuerier 查询总数的函数 () -> int
     * @return PageResult
     */
    public static <T> PageResult<T> doPage(Integer pageNum, Integer pageSize,
                                            java.util.function.Function<int[], List<T>> listQuerier,
                                            java.util.function.Supplier<Integer> countQuerier) {
        int[] params = normalize(pageNum, pageSize);
        int pn = params[0], ps = params[1];
        int offset = (pn - 1) * ps;
        List<T> list = listQuerier.apply(new int[]{offset, ps});
        int total = countQuerier.get();
        return PageResult.of(list, total, pn, ps);
    }
}
