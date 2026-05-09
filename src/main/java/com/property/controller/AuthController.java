package com.property.controller;

import com.property.entity.User;
import com.property.security.JwtUtil;
import com.property.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> body) {
        Map<String, Object> result = new HashMap<>();
        String username = body.get("username");
        String password = body.get("password");

        User user = userService.getByUsername(username);
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            result.put("code", 1);
            result.put("msg", "用户名或密码错误");
            return result;
        }

        String token = jwtUtil.generateToken(user.getUsername());
        result.put("code", 0);
        result.put("msg", "登录成功");
        result.put("token", token);
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("username", user.getUsername());
        userInfo.put("realName", user.getRealName());
        userInfo.put("role", user.getRole());
        result.put("user", userInfo);
        return result;
    }

    @GetMapping("/info")
    public Map<String, Object> info(@RequestAttribute(required = false) String username) {
        Map<String, Object> result = new HashMap<>();
        // username is set by JWT filter via SecurityContext
        org.springframework.security.core.Authentication auth =
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            result.put("code", 401);
            result.put("msg", "未登录");
            return result;
        }
        String currentUsername = (String) auth.getPrincipal();
        User user = userService.getByUsername(currentUsername);
        if (user == null) {
            result.put("code", 401);
            result.put("msg", "用户不存在");
            return result;
        }
        result.put("code", 0);
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("username", user.getUsername());
        userInfo.put("realName", user.getRealName());
        userInfo.put("role", user.getRole());
        result.put("user", userInfo);
        return result;
    }
}
