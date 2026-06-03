package com.property.controller;

import com.property.common.Result;
import com.property.dto.LoginRequest;
import com.property.entity.User;
import com.property.security.JwtUtil;
import com.property.security.LoginAttemptService;
import com.property.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证Controller
 */
@Slf4j
@RequiredArgsConstructor
@Tag(name = "认证管理", description = "登录认证与用户信息")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final LoginAttemptService loginAttemptService;

    @Operation(summary = "用户登录", description = "用户名密码登录，返回JWT Token。支持IP级别限流防暴力破解")
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody LoginRequest request,
                                             HttpServletRequest httpRequest) {
        // 以 "IP:用户名" 作为限流 key，防止同一用户名被不同 IP 暴力破解
        String clientIp = getClientIp(httpRequest);
        String rateLimitKey = clientIp + ":" + request.getUsername();

        // 检查是否已被锁定
        if (loginAttemptService.isBlocked(rateLimitKey)) {
            long remaining = loginAttemptService.getRemainingLockSeconds(rateLimitKey);
            log.warn("登录被锁定: username={}, ip={}, 剩余{}秒", request.getUsername(), clientIp, remaining);
            return Result.error("账户已锁定，请" + remaining / 60 + "分钟后重试");
        }

        User user = userService.getByUsername(request.getUsername());
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            loginAttemptService.loginFailed(rateLimitKey);
            log.warn("登录失败: username={}, ip={}", request.getUsername(), clientIp);
            return Result.error("用户名或密码错误");
        }

        // 登录成功，清除失败记录
        loginAttemptService.loginSucceeded(rateLimitKey);

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().getValue(), user.getTokenVersion());
        log.info("用户登录成功: {}", request.getUsername());

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("username", user.getUsername());
        userInfo.put("realName", user.getRealName());
        userInfo.put("role", user.getRole());
        data.put("user", userInfo);

        return Result.success("登录成功", data);
    }

    @Operation(summary = "用户登出", description = "使当前Token失效，需要认证")
    @PostMapping("/logout")
    public Result<Void> logout() {
        org.springframework.security.core.Authentication auth =
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return Result.error(401, "未登录");
        }
        String currentUsername = (String) auth.getPrincipal();
        // 递增 tokenVersion，使所有旧 token 失效
        userService.incrementTokenVersion(currentUsername);
        log.info("用户登出: {}", currentUsername);
        return Result.success("登出成功");
    }

    @Operation(summary = "获取当前用户信息", description = "根据Token解析当前登录用户的基本信息")
    @GetMapping("/info")
    public Result<Map<String, Object>> info() {
        org.springframework.security.core.Authentication auth =
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return Result.error(401, "未登录");
        }
        String currentUsername = (String) auth.getPrincipal();
        User user = userService.getByUsername(currentUsername);
        if (user == null) {
            return Result.error(401, "用户不存在");
        }

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("username", user.getUsername());
        userInfo.put("realName", user.getRealName());
        userInfo.put("role", user.getRole());

        return Result.success(userInfo);
    }

    /**
     * 获取客户端真实 IP，优先从代理头中读取
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !ip.isBlank()) {
            // X-Forwarded-For 可能包含多个 IP，取第一个
            ip = ip.split(",")[0].trim();
        }
        if (ip == null || ip.isBlank()) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isBlank()) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
