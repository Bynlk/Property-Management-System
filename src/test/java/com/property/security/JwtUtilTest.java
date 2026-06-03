package com.property.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JwtUtil 单元测试")
class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() throws Exception {
        jwtUtil = new JwtUtil();
        // 通过反射注入 secret 和 expiration
        Field secretField = JwtUtil.class.getDeclaredField("secret");
        secretField.setAccessible(true);
        secretField.set(jwtUtil, Base64.getEncoder().encodeToString("test-secret-key-for-unit-testing-32b!".getBytes()));

        Field expirationField = JwtUtil.class.getDeclaredField("expiration");
        expirationField.setAccessible(true);
        expirationField.set(jwtUtil, 86400000L); // 24小时
    }

    @Nested
    @DisplayName("generateToken + 解析")
    class GenerateAndParseTest {

        @Test
        @DisplayName("生成后解析出正确用户名")
        void parsesUsername() {
            String token = jwtUtil.generateToken("admin", "admin", 0);
            assertEquals("admin", jwtUtil.getUsernameFromToken(token));
        }

        @Test
        @DisplayName("生成后解析出正确角色")
        void parsesRole() {
            String token = jwtUtil.generateToken("admin", "admin", 0);
            assertEquals("admin", jwtUtil.getRoleFromToken(token));
        }

        @Test
        @DisplayName("生成后解析出正确tokenVersion")
        void parsesTokenVersion() {
            String token = jwtUtil.generateToken("user1", "user", 3);
            assertEquals(3, jwtUtil.getTokenVersionFromToken(token));
        }

        @Test
        @DisplayName("tokenVersion为0时正确解析")
        void parsesZeroTokenVersion() {
            String token = jwtUtil.generateToken("user1", "user", 0);
            assertEquals(0, jwtUtil.getTokenVersionFromToken(token));
        }

        @Test
        @DisplayName("不同用户名生成不同token")
        void differentUsers_differentTokens() {
            String token1 = jwtUtil.generateToken("admin", "admin", 0);
            String token2 = jwtUtil.generateToken("user1", "user", 0);
            assertNotEquals(token1, token2);
        }
    }

    @Nested
    @DisplayName("validateToken")
    class ValidateTokenTest {

        @Test
        @DisplayName("有效token返回true")
        void validToken_returnsTrue() {
            String token = jwtUtil.generateToken("admin", "admin", 0);
            assertTrue(jwtUtil.validateToken(token));
        }

        @Test
        @DisplayName("篡改token返回false")
        void tamperedToken_returnsFalse() {
            String token = jwtUtil.generateToken("admin", "admin", 0);
            // 篡改token内容
            String tampered = token.substring(0, token.length() - 5) + "XXXXX";
            assertFalse(jwtUtil.validateToken(tampered));
        }

        @Test
        @DisplayName("空字符串返回false")
        void emptyString_returnsFalse() {
            assertFalse(jwtUtil.validateToken(""));
        }

        @Test
        @DisplayName("随机字符串返回false")
        void randomString_returnsFalse() {
            assertFalse(jwtUtil.validateToken("not.a.jwt.token"));
        }

        @Test
        @DisplayName("null返回false")
        void null_returnsFalse() {
            assertFalse(jwtUtil.validateToken(null));
        }
    }

    @Nested
    @DisplayName("generateToken 额外验证")
    class GenerateTokenEdgeCasesTest {

        @Test
        @DisplayName("角色为user时正确解析")
        void userRole_parsesCorrectly() {
            String token = jwtUtil.generateToken("normalUser", "user", 1);
            assertEquals("user", jwtUtil.getRoleFromToken(token));
            assertEquals("normalUser", jwtUtil.getUsernameFromToken(token));
            assertEquals(1, jwtUtil.getTokenVersionFromToken(token));
        }
    }
}
