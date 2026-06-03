package com.property.service.impl;

import com.property.common.BusinessException;
import com.property.entity.User;
import com.property.enums.UserRole;
import com.property.mapper.UserMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserServiceImpl 单元测试")
class UserServiceImplTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Nested
    @DisplayName("getByUsername")
    class GetByUsernameTest {

        @Test
        @DisplayName("存在时返回用户")
        void returnsUser_whenExists() {
            User user = User.builder().id(1).username("admin").role(UserRole.ADMIN).build();
            when(userMapper.selectByUsername("admin")).thenReturn(user);

            User result = userService.getByUsername("admin");

            assertNotNull(result);
            assertEquals("admin", result.getUsername());
        }

        @Test
        @DisplayName("不存在时返回null")
        void returnsNull_whenNotExists() {
            when(userMapper.selectByUsername("nobody")).thenReturn(null);

            assertNull(userService.getByUsername("nobody"));
        }
    }

    @Nested
    @DisplayName("add - 密码复杂度校验")
    class AddPasswordValidationTest {

        @Test
        @DisplayName("密码过短（少于8位）抛出异常")
        void throws_whenTooShort() {
            User user = User.builder().username("test").password("Ab1").build();

            BusinessException ex = assertThrows(BusinessException.class, () -> userService.add(user));
            assertTrue(ex.getMessage().contains("少于8位"));
        }

        @Test
        @DisplayName("缺少小写字母抛出异常")
        void throws_whenNoLowercase() {
            User user = User.builder().username("test").password("ABCDEFG1").build();

            BusinessException ex = assertThrows(BusinessException.class, () -> userService.add(user));
            assertTrue(ex.getMessage().contains("小写字母"));
        }

        @Test
        @DisplayName("缺少大写字母抛出异常")
        void throws_whenNoUppercase() {
            User user = User.builder().username("test").password("abcdefg1").build();

            BusinessException ex = assertThrows(BusinessException.class, () -> userService.add(user));
            assertTrue(ex.getMessage().contains("大写字母"));
        }

        @Test
        @DisplayName("缺少数字抛出异常")
        void throws_whenNoDigit() {
            User user = User.builder().username("test").password("Abcdefgh").build();

            BusinessException ex = assertThrows(BusinessException.class, () -> userService.add(user));
            assertTrue(ex.getMessage().contains("数字"));
        }

        @Test
        @DisplayName("null密码抛出异常")
        void throws_whenNull() {
            User user = User.builder().username("test").password(null).build();

            assertThrows(BusinessException.class, () -> userService.add(user));
        }

        @Test
        @DisplayName("合法密码成功新增")
        void succeeds_whenValidPassword() {
            User user = User.builder().username("test").password("Admin123").build();
            when(passwordEncoder.encode("Admin123")).thenReturn("$2a$10$encoded");
            when(userMapper.insert(any(User.class))).thenReturn(1);

            int result = userService.add(user);

            assertEquals(1, result);
            assertEquals("$2a$10$encoded", user.getPassword());
            assertEquals(0, user.getTokenVersion());
            verify(userMapper).insert(user);
        }
    }

    @Nested
    @DisplayName("updatePassword - 密码复杂度校验")
    class UpdatePasswordValidationTest {

        @Test
        @DisplayName("弱密码抛出异常")
        void throws_whenWeakPassword() {
            assertThrows(BusinessException.class,
                    () -> userService.updatePassword("admin", "123"));
        }

        @Test
        @DisplayName("合法密码成功更新")
        void succeeds_whenValidPassword() {
            when(passwordEncoder.encode("NewPass1")).thenReturn("$2a$10$newencoded");
            when(userMapper.updatePasswordAndIncrementVersion("admin", "$2a$10$newencoded")).thenReturn(1);

            userService.updatePassword("admin", "NewPass1");

            verify(userMapper).updatePasswordAndIncrementVersion("admin", "$2a$10$newencoded");
        }
    }

    @Nested
    @DisplayName("incrementTokenVersion")
    class IncrementTokenVersionTest {

        @Test
        @DisplayName("成功递增token版本号")
        void succeeds() {
            when(userMapper.incrementTokenVersion("admin")).thenReturn(1);

            userService.incrementTokenVersion("admin");

            verify(userMapper).incrementTokenVersion("admin");
        }
    }
}
