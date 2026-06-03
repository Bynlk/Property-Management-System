package com.property.security;

import com.property.entity.User;
import com.property.enums.UserRole;
import com.property.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtAuthenticationFilter 单元测试")
class JwtAuthenticationFilterTest {

    @Mock private JwtUtil jwtUtil;
    @Mock private UserService userService;
    @Mock private FilterChain filterChain;
    @InjectMocks private JwtAuthenticationFilter filter;

    private final MockHttpServletRequest request = new MockHttpServletRequest();
    private final MockHttpServletResponse response = new MockHttpServletResponse();

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Nested
    @DisplayName("无Token场景")
    class NoTokenTest {

        @Test
        @DisplayName("无Authorization头 - 直接放行")
        void noAuthHeader_passesThrough() throws ServletException, IOException {
            filter.doFilterInternal(request, response, filterChain);
            verify(filterChain).doFilter(request, response);
            assertNull(SecurityContextHolder.getContext().getAuthentication());
        }

        @Test
        @DisplayName("Authorization头不以Bearer开头 - 直接放行")
        void nonBearerHeader_passesThrough() throws ServletException, IOException {
            request.addHeader("Authorization", "Basic abc123");
            filter.doFilterInternal(request, response, filterChain);
            verify(filterChain).doFilter(request, response);
            assertNull(SecurityContextHolder.getContext().getAuthentication());
        }
    }

    @Nested
    @DisplayName("无效Token场景")
    class InvalidTokenTest {

        @Test
        @DisplayName("Token验证失败 - 不设置认证")
        void invalidToken_noAuth() throws ServletException, IOException {
            request.addHeader("Authorization", "Bearer invalid-token");
            when(jwtUtil.validateToken("invalid-token")).thenReturn(false);

            filter.doFilterInternal(request, response, filterChain);

            verify(filterChain).doFilter(request, response);
            assertNull(SecurityContextHolder.getContext().getAuthentication());
        }

        @Test
        @DisplayName("用户不存在 - 不设置认证")
        void userNotFound_noAuth() throws ServletException, IOException {
            request.addHeader("Authorization", "Bearer valid-token");
            when(jwtUtil.validateToken("valid-token")).thenReturn(true);
            when(jwtUtil.getUsernameFromToken("valid-token")).thenReturn("ghost");
            when(userService.getByUsername("ghost")).thenReturn(null);

            filter.doFilterInternal(request, response, filterChain);

            verify(filterChain).doFilter(request, response);
            assertNull(SecurityContextHolder.getContext().getAuthentication());
        }

        @Test
        @DisplayName("tokenVersion不匹配 - 不设置认证")
        void tokenVersionMismatch_noAuth() throws ServletException, IOException {
            request.addHeader("Authorization", "Bearer valid-token");
            when(jwtUtil.validateToken("valid-token")).thenReturn(true);
            when(jwtUtil.getUsernameFromToken("valid-token")).thenReturn("admin");
            when(jwtUtil.getRoleFromToken("valid-token")).thenReturn("admin");
            when(jwtUtil.getTokenVersionFromToken("valid-token")).thenReturn(0);
            User user = User.builder().id(1).username("admin").role(UserRole.ADMIN).tokenVersion(2).build();
            when(userService.getByUsername("admin")).thenReturn(user);

            filter.doFilterInternal(request, response, filterChain);

            verify(filterChain).doFilter(request, response);
            assertNull(SecurityContextHolder.getContext().getAuthentication());
        }
    }

    @Nested
    @DisplayName("有效Token场景")
    class ValidTokenTest {

        @Test
        @DisplayName("有效Token + 匹配tokenVersion - 设置认证")
        void validToken_setsAuth() throws ServletException, IOException {
            request.addHeader("Authorization", "Bearer valid-token");
            when(jwtUtil.validateToken("valid-token")).thenReturn(true);
            when(jwtUtil.getUsernameFromToken("valid-token")).thenReturn("admin");
            when(jwtUtil.getRoleFromToken("valid-token")).thenReturn("admin");
            when(jwtUtil.getTokenVersionFromToken("valid-token")).thenReturn(0);
            User user = User.builder().id(1).username("admin").role(UserRole.ADMIN).tokenVersion(0).build();
            when(userService.getByUsername("admin")).thenReturn(user);

            filter.doFilterInternal(request, response, filterChain);

            assertNotNull(SecurityContextHolder.getContext().getAuthentication());
            assertEquals("admin", SecurityContextHolder.getContext().getAuthentication().getPrincipal());
            verify(filterChain).doFilter(request, response);
        }

        @Test
        @DisplayName("DB角色覆盖JWT角色")
        void dbRoleOverridesJwtRole() throws ServletException, IOException {
            request.addHeader("Authorization", "Bearer valid-token");
            when(jwtUtil.validateToken("valid-token")).thenReturn(true);
            when(jwtUtil.getUsernameFromToken("valid-token")).thenReturn("user1");
            when(jwtUtil.getRoleFromToken("valid-token")).thenReturn("user"); // JWT says "user"
            when(jwtUtil.getTokenVersionFromToken("valid-token")).thenReturn(0);
            User user = User.builder().id(1).username("user1").role(UserRole.ADMIN).tokenVersion(0).build(); // DB says "admin"
            when(userService.getByUsername("user1")).thenReturn(user);

            filter.doFilterInternal(request, response, filterChain);

            assertNotNull(SecurityContextHolder.getContext().getAuthentication());
            assertTrue(SecurityContextHolder.getContext().getAuthentication()
                    .getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
        }
    }

    @Nested
    @DisplayName("清理")
    class CleanupTest {
        @org.junit.jupiter.api.AfterEach
        void clearContext() {
            SecurityContextHolder.clearContext();
        }
    }
}
