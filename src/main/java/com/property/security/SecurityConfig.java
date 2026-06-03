package com.property.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import org.springframework.security.config.Customizer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Value("${app.cors.allowed-origins:http://localhost:3000}")
    private String allowedOrigins;

    @Value("${springdoc.swagger-ui.enabled:true}")
    private boolean swaggerEnabled;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .headers(headers -> headers
                .contentTypeOptions(Customizer.withDefaults())
                .frameOptions(frame -> frame.deny())
                .xssProtection(Customizer.withDefaults())
                .contentSecurityPolicy(csp -> csp.policyDirectives(
                    "default-src 'self'; script-src 'self' 'unsafe-inline'; style-src 'self' 'unsafe-inline'; img-src 'self' data:; font-src 'self' data:"
                ))
                .httpStrictTransportSecurity(hsts -> hsts
                    .includeSubDomains(true)
                    .maxAgeInSeconds(31536000)
                )
            )
            .authorizeHttpRequests(auth -> {
                auth
                    .requestMatchers("/api/auth/**").permitAll()
                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll();
                // Swagger UI & OpenAPI 文档 - 仅在启用时公开（生产环境通过 springdoc.swagger-ui.enabled=false 关闭）
                if (swaggerEnabled) {
                    auth.requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll();
                }
                auth
                    // Druid 监控页面 - 仅限 admin 角色
                    .requestMatchers("/druid/**").hasRole("ADMIN")
                    // Actuator 端点 - 仅限 admin 角色
                    .requestMatchers("/actuator/**").hasRole("ADMIN")
                    // 写操作（新增/修改/删除）仅限 admin
                    .requestMatchers(HttpMethod.DELETE, "/api/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/api/**").hasRole("ADMIN")
                    // 新增操作：仅限 admin（POST 到业务资源根路径）
                    .requestMatchers(HttpMethod.POST, "/api/owner/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.POST, "/api/employee/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.POST, "/api/house/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.POST, "/api/fee/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.POST, "/api/parking/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.POST, "/api/complaint/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.POST, "/api/repair/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.POST, "/api/duty/**").hasRole("ADMIN")
                    // IDOR 保护：按业主ID查询关联数据仅限 admin
                    .requestMatchers(HttpMethod.GET, "/api/*/owner/**").hasRole("ADMIN")
                    // 其他请求需要认证
                    .anyRequest().authenticated();
            })
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(401);
                    response.setContentType("application/json;charset=UTF-8");
                    Map<String, Object> result = new HashMap<>();
                    result.put("code", 401);
                    result.put("msg", "未登录或Token已过期");
                    response.getWriter().write(new ObjectMapper().writeValueAsString(result));
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.setStatus(403);
                    response.setContentType("application/json;charset=UTF-8");
                    Map<String, Object> result = new HashMap<>();
                    result.put("code", 403);
                    result.put("msg", "权限不足，仅管理员可执行此操作");
                    response.getWriter().write(new ObjectMapper().writeValueAsString(result));
                })
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of(allowedOrigins.split(",")));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
