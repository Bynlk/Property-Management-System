package com.property.security;

import com.property.entity.User;
import com.property.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserService userService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserService userService) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            if (jwtUtil.validateToken(token)) {
                String username = jwtUtil.getUsernameFromToken(token);
                String role = jwtUtil.getRoleFromToken(token);

                // 验证 token 版本号：如果用户已修改密码，旧 token 将失效
                User user = userService.getByUsername(username);
                if (user == null) {
                    filterChain.doFilter(request, response);
                    return;
                }
                int tokenVersion = jwtUtil.getTokenVersionFromToken(token);
                int currentVersion = user.getTokenVersion() != null ? user.getTokenVersion() : 0;
                if (tokenVersion != currentVersion) {
                    filterChain.doFilter(request, response);
                    return;
                }

                // 根据角色创建权限列表（优先使用 DB 中的角色，确保角色变更立即生效）
                String effectiveRole = role;
                if (user.getRole() != null) {
                    String dbRole = user.getRole().getValue();
                    if (!dbRole.equalsIgnoreCase(role)) {
                        effectiveRole = dbRole;
                    }
                }
                List<SimpleGrantedAuthority> authorities = effectiveRole != null
                        ? List.of(new SimpleGrantedAuthority("ROLE_" + effectiveRole.toUpperCase()))
                        : Collections.emptyList();

                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(username, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        filterChain.doFilter(request, response);
    }
}
