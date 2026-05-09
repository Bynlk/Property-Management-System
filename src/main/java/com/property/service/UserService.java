package com.property.service;

import com.property.entity.User;

public interface UserService {
    User getByUsername(String username);
    int add(User user);
}
