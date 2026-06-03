package com.property.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("LoginAttemptService 单元测试")
class LoginAttemptServiceTest {

    private final LoginAttemptService service = new LoginAttemptService();

    @Nested
    @DisplayName("loginFailed")
    class LoginFailedTest {

        @Test
        @DisplayName("未达阈值时不锁定")
        void notBlocked_beforeThreshold() {
            for (int i = 0; i < 4; i++) {
                service.loginFailed("user1:192.168.1.1");
            }
            assertFalse(service.isBlocked("user1:192.168.1.1"));
        }

        @Test
        @DisplayName("达到5次后锁定")
        void blocked_afterMaxAttempts() {
            for (int i = 0; i < 5; i++) {
                service.loginFailed("user2:192.168.1.1");
            }
            assertTrue(service.isBlocked("user2:192.168.1.1"));
        }

        @Test
        @DisplayName("超过5次仍然锁定")
        void stillBlocked_afterExceedingMax() {
            for (int i = 0; i < 8; i++) {
                service.loginFailed("user3:192.168.1.1");
            }
            assertTrue(service.isBlocked("user3:192.168.1.1"));
        }
    }

    @Nested
    @DisplayName("isBlocked")
    class IsBlockedTest {

        @Test
        @DisplayName("未失败过返回false")
        void returnsFalse_whenNoAttempts() {
            assertFalse(service.isBlocked("unknown:10.0.0.1"));
        }

        @Test
        @DisplayName("锁定后返回true")
        void returnsTrue_whenLocked() {
            String key = "user4:192.168.1.1";
            for (int i = 0; i < 5; i++) {
                service.loginFailed(key);
            }
            assertTrue(service.isBlocked(key));
        }
    }

    @Nested
    @DisplayName("loginSucceeded")
    class LoginSucceededTest {

        @Test
        @DisplayName("登录成功清除锁定状态")
        void clearsLockState() {
            String key = "user5:192.168.1.1";
            for (int i = 0; i < 5; i++) {
                service.loginFailed(key);
            }
            assertTrue(service.isBlocked(key));

            service.loginSucceeded(key);
            assertFalse(service.isBlocked(key));
        }

        @Test
        @DisplayName("登录成功后重新计数")
        void resetsCounter() {
            String key = "user6:192.168.1.1";
            for (int i = 0; i < 3; i++) {
                service.loginFailed(key);
            }
            service.loginSucceeded(key);
            // 清除后需要再失败5次才会锁定
            for (int i = 0; i < 4; i++) {
                service.loginFailed(key);
            }
            assertFalse(service.isBlocked(key));
        }
    }

    @Nested
    @DisplayName("getRemainingLockSeconds")
    class GetRemainingLockSecondsTest {

        @Test
        @DisplayName("未锁定时返回0")
        void returnsZero_whenNotLocked() {
            assertEquals(0, service.getRemainingLockSeconds("nobody:10.0.0.1"));
        }

        @Test
        @DisplayName("锁定时返回正数")
        void returnsPositive_whenLocked() {
            String key = "user7:192.168.1.1";
            for (int i = 0; i < 5; i++) {
                service.loginFailed(key);
            }
            long remaining = service.getRemainingLockSeconds(key);
            assertTrue(remaining > 0);
            assertTrue(remaining <= 900); // 15分钟 = 900秒
        }
    }

    @Nested
    @DisplayName("不同key独立计数")
    class IndependentKeysTest {

        @Test
        @DisplayName("key A 失败不影响 key B")
        void independentCounting() {
            String keyA = "userA:192.168.1.1";
            String keyB = "userB:192.168.1.2";
            for (int i = 0; i < 5; i++) {
                service.loginFailed(keyA);
            }
            assertTrue(service.isBlocked(keyA));
            assertFalse(service.isBlocked(keyB));
        }
    }
}
