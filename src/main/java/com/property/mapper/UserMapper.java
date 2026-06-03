package com.property.mapper;

import com.property.entity.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {

    User selectByUsername(@Param("username") String username);

    int insert(User user);

    int updatePasswordAndIncrementVersion(@Param("username") String username, @Param("password") String password);

    int incrementTokenVersion(@Param("username") String username);
}
