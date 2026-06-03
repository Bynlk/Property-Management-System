package com.property.security;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 登录失败速率限制服务
 * 基于内存记录每个用户名的失败尝试次数，超过阈值后锁定账户
 */
@Component
public class LoginAttemptService {

    /** 最大失败尝试次数 */
    private static final int MAX_ATTEMPTS = 5;

    /** 锁定时间（毫秒），15分钟 */
    private static final long LOCK_DURATION_MS = 15 * 60 * 1000L;

    /** 记录失败尝试次数 */
    private final ConcurrentHashMap<String, Integer> attemptsCache = new ConcurrentHashMap<>();

    /** 记录锁定开始时间 */
    private final ConcurrentHashMap<String, Long> lockTimeCache = new ConcurrentHashMap<>();

    /**
     * 记录一次登录失败
     */
    public void loginFailed(String key) {
        int current = attemptsCache.getOrDefault(key, 0) + 1;
        attemptsCache.put(key, current);
        if (current >= MAX_ATTEMPTS) {
            lockTimeCache.put(key, System.currentTimeMillis());
        }
    }

    /**
     * 登录成功时清除记录
     */
    public void loginSucceeded(String key) {
        attemptsCache.remove(key);
        lockTimeCache.remove(key);
    }

    /**
     * 判断是否已被锁定
     */
    public boolean isBlocked(String key) {
        Long lockTime = lockTimeCache.get(key);
        if (lockTime == null) {
            return false;
        }
        // 锁定时间已过期，自动解锁
        if (System.currentTimeMillis() - lockTime > LOCK_DURATION_MS) {
            attemptsCache.remove(key);
            lockTimeCache.remove(key);
            return false;
        }
        return true;
    }

    /**
     * 获取剩余锁定时间（秒）
     */
    public long getRemainingLockSeconds(String key) {
        Long lockTime = lockTimeCache.get(key);
        if (lockTime == null) {
            return 0;
        }
        long elapsed = System.currentTimeMillis() - lockTime;
        long remaining = (LOCK_DURATION_MS - elapsed) / 1000;
        return Math.max(remaining, 0);
    }
}
