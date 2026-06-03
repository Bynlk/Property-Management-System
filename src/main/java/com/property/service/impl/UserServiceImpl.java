package com.property.service.impl;

import com.property.common.BusinessException;
import com.property.entity.User;
import com.property.mapper.UserMapper;
import com.property.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User getByUsername(String username) {
        return userMapper.selectByUsername(username);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int add(User user) {
        validatePasswordComplexity(user.getPassword());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setTokenVersion(0);
        return userMapper.insert(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePassword(String username, String newPassword) {
        validatePasswordComplexity(newPassword);
        String encoded = passwordEncoder.encode(newPassword);
        // 递增 token_version，使所有旧 token 失效
        userMapper.updatePasswordAndIncrementVersion(username, encoded);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void incrementTokenVersion(String username) {
        userMapper.incrementTokenVersion(username);
    }

    /**
     * 密码复杂度校验：至少8位，包含大小写字母和数字
     */
    private void validatePasswordComplexity(String password) {
        if (password == null || password.length() < 8) {
            throw new BusinessException("密码长度不能少于8位");
        }
        if (!password.matches(".*[a-z].*")) {
            throw new BusinessException("密码必须包含小写字母");
        }
        if (!password.matches(".*[A-Z].*")) {
            throw new BusinessException("密码必须包含大写字母");
        }
        if (!password.matches(".*\\d.*")) {
            throw new BusinessException("密码必须包含数字");
        }
    }
}
